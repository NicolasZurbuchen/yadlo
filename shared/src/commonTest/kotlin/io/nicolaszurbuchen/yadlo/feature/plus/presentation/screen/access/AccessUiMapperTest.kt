package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.common.content.domain.model.TransportMode
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.access_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(AccessState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_loadedWithNoSection_saysSo() {
        val model = AccessState(transport = null, hasLoaded = true).toUiModel()

        assertEquals(UiText.Resource(Res.string.access_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_modesKeepTheContentsOrder() {
        val model = loaded()

        assertEquals(listOf("pied", "bus-nuit"), model.modes.map { it.id })
    }

    @Test
    fun toUiModel_aNight_readsAsOneLineOfTimesRatherThanOneRowPerBus() {
        val night = loaded().modes.last().nights.single()

        // Seven departures in two lines instead of seven rows is the difference between a screen
        // you can read at 02:00 and one you have to scroll.
        assertEquals("00:59 · 01:30 · 03:00", night.times)
    }

    @Test
    fun toUiModel_aNoteOnADeparture_keepsTheTimeItIsAbout() {
        val night = loaded().modes.last().nights.single()

        // The one that matters: without its time, "pas de correspondance" is a footnote about
        // nothing, and getting it wrong means sleeping at Morges.
        assertEquals(listOf("03:00 — Pas de correspondance pour Lausanne."), night.notes)
    }

    @Test
    fun toUiModel_departuresWithNoNotes_produceNoFootnotes() {
        val night =
            AccessState(hasLoaded = true, transport = transport(notes = false)).toUiModel().modes.last().nights.single()

        assertTrue(night.notes.isEmpty())
    }

    @Test
    fun toUiModel_aModeWithNoTimetable_hasNoNightBlocks() {
        assertTrue(loaded().modes.first().nights.isEmpty())
    }

    @Test
    fun toUiModel_aModesLinks_keepBothLinesForTheTile() {
        val link = loaded().modes.first().links.single()

        assertEquals("Horaires ligne 701", link.label)
        assertEquals("PDF · MBC", link.sublabel)
    }

    @Test
    fun toUiModel_aModeWithNoProse_isStillListed() {
        val model =
            AccessState(
                hasLoaded = true,
                transport =
                    Transport(
                        modes =
                            listOf(
                                TransportMode(
                                    id = "velo",
                                    name = "À vélo",
                                    body = null,
                                    links = emptyList(),
                                    departures = emptyList(),
                                ),
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
            ).toUiModel()

        assertNull(model.modes.single().body)
        assertEquals("À vélo", model.modes.single().name)
    }

    private fun loaded() = AccessState(hasLoaded = true, transport = transport(notes = true)).toUiModel()

    private fun transport(notes: Boolean) =
        Transport(
            provenance = Provenance.CONFIRMED,
            modes =
                listOf(
                    TransportMode(
                        id = "pied",
                        name = "À pied",
                        body = "35 minutes depuis Morges.",
                        links =
                            listOf(
                                InfoLink(
                                    id = "701",
                                    label = "Horaires ligne 701",
                                    sublabel = "PDF · MBC",
                                    url = "https://example.ch/701.pdf",
                                ),
                            ),
                        departures = emptyList(),
                    ),
                    TransportMode(
                        id = "bus-nuit",
                        name = "Bus de nuit",
                        body = "Vers Morges, gare.",
                        links = emptyList(),
                        departures =
                            listOf(
                                TransportMode.Departure(
                                    id = "samedi",
                                    night = "Samedi",
                                    times =
                                        listOf(
                                            TransportMode.Departure.Time(time = "00:59", note = null),
                                            TransportMode.Departure.Time(time = "01:30", note = null),
                                            TransportMode.Departure.Time(
                                                time = "03:00",
                                                note = "Pas de correspondance pour Lausanne.".takeIf { notes },
                                            ),
                                        ),
                                ),
                            ),
                    ),
                ),
        )
}
