package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.core.content.domain.model.TransportMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObserveTransportUseCaseTest {
    @Test
    fun invoke_noSectionPublished_isNull() =
        runTest {
            assertNull(ObserveTransportUseCase(FakeContentRepository().apply { emitStatus(ready()) })().first())
        }

    @Test
    fun invoke_modesKeepTheContentsOrder() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withTransport())) }

            // Chronological rather than alphabetical: coming before going home, the way the page is
            // read before leaving the house.
            assertEquals(
                listOf("pied", "bus", "bus-nuit"),
                ObserveTransportUseCase(repository)().first()?.modes?.map { it.id },
            )
        }

    @Test
    fun invoke_aModeWithNoTimetable_hasAnEmptyListRatherThanANullOne() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withTransport())) }

            val onFoot = ObserveTransportUseCase(repository)().first()?.modes?.first()

            // Collapsed in the mapper, asserted here: no screen should have to tell "null" from
            // "empty" for the same fact.
            assertTrue(onFoot?.departures?.isEmpty() == true)
        }

    @Test
    fun invoke_theNightBus_keepsItsNoteAttachedToItsTime() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withTransport())) }

            val night = ObserveTransportUseCase(repository)().first()?.modes?.last()?.departures?.single()

            assertEquals(listOf("00:59", "03:00"), night?.times?.map { it.time })
            assertEquals("Pas de correspondance pour Lausanne.", night?.times?.last()?.note)
        }

    @Test
    fun invoke_aModesLinks_carryBothLinesTheContentWrote() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withTransport())) }

            val bus = ObserveTransportUseCase(repository)().first()?.modes?.get(1)

            assertEquals("Horaires ligne 701", bus?.links?.single()?.label)
            assertEquals("PDF · MBC", bus?.links?.single()?.sublabel)
        }

    private fun withTransport() =
        festival {
            copy(
                transport =
                    Transport(
                        modes =
                            listOf(
                                TransportMode(
                                    id = "pied",
                                    name = "À pied",
                                    body = "35 minutes depuis Morges.",
                                    facts = emptyList(),
                                    links = emptyList(),
                                    departures = emptyList(),
                                ),
                                TransportMode(
                                    id = "bus",
                                    name = "En bus",
                                    body = "Lignes 701 et 705.",
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
                                                        TransportMode.Departure.Time(
                                                            time = "03:00",
                                                            note = "Pas de correspondance pour Lausanne.",
                                                        ),
                                                    ),
                                            ),
                                        ),
                                ),
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }
}
