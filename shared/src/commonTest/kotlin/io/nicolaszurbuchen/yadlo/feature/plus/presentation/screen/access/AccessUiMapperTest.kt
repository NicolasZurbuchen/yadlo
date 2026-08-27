package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import io.nicolaszurbuchen.yadlo.core.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.core.content.domain.model.TransportMode
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFactMarkUiModel
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
                                    facts = emptyList(),
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

    @Test
    fun toUiModel_aCaveat_isMarkedApartFromWhatTheSiteActuallyOffers() {
        val facts =
            listOf(
                TransportMode.Fact(id = "places", text = "Places limitées", caveat = true),
                TransportMode.Fact(id = "reservees", text = "Deux places réservées", caveat = false),
            )
        val state =
            AccessState(
                hasLoaded = true,
                transport =
                    Transport(
                        provenance = Provenance.CONFIRMED,
                        modes =
                            listOf(
                                TransportMode(
                                    id = "voiture",
                                    name = "En voiture",
                                    body = null,
                                    facts = facts,
                                    links = emptyList(),
                                    departures = emptyList(),
                                ),
                            ),
                    ),
            )

        // Both are true and they are not the same kind of true: one is what the site offers, the
        // other is what will go wrong. In a paragraph they weigh the same.
        assertEquals(
            listOf(YadloFactMarkUiModel.INFO, YadloFactMarkUiModel.CHECK),
            state.toUiModel().modes.single().facts.map { it.mark },
        )
    }

    @Test
    fun toUiModel_noFactIsEverARefusal() {
        val state =
            AccessState(
                hasLoaded = true,
                transport =
                    Transport(
                        provenance = Provenance.CONFIRMED,
                        modes =
                            listOf(
                                TransportMode(
                                    id = "bus",
                                    name = "Venir en bus",
                                    body = null,
                                    facts = listOf(TransportMode.Fact(id = "lignes", text = "701 et 705", caveat = false)),
                                    links = emptyList(),
                                    departures = emptyList(),
                                ),
                            ),
                    ),
            )

        // A way of getting here that does not exist is left out of the content rather than
        // published as a ✕, so this screen has two marks where paiement has three.
        assertTrue(state.toUiModel().modes.single().facts.none { it.mark == YadloFactMarkUiModel.CROSS })
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
                        facts = emptyList(),
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
                        facts = emptyList(),
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
