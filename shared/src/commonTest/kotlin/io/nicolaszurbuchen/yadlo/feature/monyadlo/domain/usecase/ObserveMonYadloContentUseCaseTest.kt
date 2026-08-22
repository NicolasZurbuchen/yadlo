package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class ObserveMonYadloContentUseCaseTest {
    private val contentRepository = FakeContentRepository()
    private val planRepository = FakePlanRepository()
    private val useCase = ObserveMonYadloContentUseCase(contentRepository, planRepository)

    @Test
    fun invoke_beforeTheBundleIsReady_emitsNothingRatherThanAnEmptyPlan() =
        runTest {
            useCase().test {
                expectNoEvents()

                contentRepository.emitStatus(ContentStatus.Loading)
                expectNoEvents()

                cancel()
            }
        }

    @Test
    fun invoke_nothingSaved_emitsAPlanWithNoDaysAtAll() =
        runTest {
            useCase().test {
                contentRepository.emitStatus(ready())

                val content = awaitItem()

                assertTrue(content.days.isEmpty())
                assertEquals(0, content.wishlistCount)
            }
        }

    @Test
    fun invoke_savedSlots_areGroupedUnderTheDayTheyWereAuthoredOn() =
        runTest {
            planRepository.emitSaved(listOf(savedSlot("2026:dj-alf-fri"), savedSlot("2026:caesure-sat")))

            useCase().test {
                contentRepository.emitStatus(ready())

                val days = awaitItem().days

                assertEquals(listOf("2026:fri", "2026:sat"), days.map { it.id })
                assertEquals(listOf("DJ ALF"), days.first().slots.map { it.name })
            }
        }

    @Test
    fun invoke_aDaysWindow_isMeasuredOverEverythingProgrammedOnItRatherThanOverWhatWasSaved() =
        runTest {
            planRepository.emitSaved(listOf(savedSlot("2026:caesure-sat")))

            useCase().test {
                contentRepository.emitStatus(ready())

                val saturday = awaitItem().days.single()

                // One 20:30 set is saved, and the day still opens at 10:00 because that is when the
                // first thing on it starts. A Plan scaled to what you happened to keep would put the
                // same Slot at a different point on the Programme and on this screen, and the axis
                // is the one thing the two have to agree on.
                assertEquals(Instant.parse("2026-07-11T10:00:00+02:00"), saturday.windowStart)
                assertEquals(Instant.parse("2026-07-12T02:00:00+02:00"), saturday.windowEnd)
            }
        }

    @Test
    fun invoke_aDayWithNothingSavedOnIt_isAbsentRatherThanEmpty() =
        runTest {
            planRepository.emitSaved(listOf(savedSlot("2026:caesure-sat")))

            useCase().test {
                contentRepository.emitStatus(ready())

                // Three headers with one row under them says less about someone's festival than
                // one header does.
                assertEquals(listOf("2026:sat"), awaitItem().days.map { it.id })
            }
        }

    @Test
    fun invoke_slotsOfOneDay_areOrderedByStartThenByTheShorterOfTwo() =
        runTest {
            planRepository.emitSaved(
                listOf(savedSlot("2026:silent-party-sat"), savedSlot("2026:caesure-sat"), savedSlot("2026:yoga-sat")),
            )

            useCase().test {
                contentRepository.emitStatus(ready())

                val saturday = awaitItem().days.single()

                // Yoga and Caesure both start at 20:30; the shorter one reads first, which is the
                // order someone scanning for "what is on now" is reading in.
                assertEquals(listOf("Caesure", "Yoga au coucher", "Silent Party"), saturday.slots.map { it.name })
            }
        }

    @Test
    fun invoke_aSavedSlotTheEditionNoLongerDeclares_disappearsWithoutBeingDeleted() =
        runTest {
            planRepository.emitSaved(listOf(savedSlot("2026:cancelled-fri")))

            useCase().test {
                contentRepository.emitStatus(ready())

                // Never a silent removal from someone's Plan: the id stays saved and starts
                // matching again the moment the content brings the Slot back.
                assertTrue(awaitItem().days.isEmpty())
                assertEquals(1, planRepository.observeSaved().first().size)
            }
        }

    @Test
    fun invoke_aSavedStand_neverLandsOnTheTimeline() =
        runTest {
            planRepository.emitSaved(listOf(SavedItem("vegan-fabrik", SavedKind.STAND, "2026")))

            useCase().test {
                contentRepository.emitStatus(ready())

                val content = awaitItem()

                // The bar being open from 12:00 to 02:00 is not a fourteen-hour appointment.
                assertTrue(content.days.isEmpty())
                assertEquals(1, content.wishlistCount)
            }
        }

    @Test
    fun invoke_wishlistCount_ignoresASavedStandTheEditionNoLongerDeclares() =
        runTest {
            planRepository.emitSaved(
                listOf(
                    SavedItem("vegan-fabrik", SavedKind.STAND, "2026"),
                    SavedItem("gone-since-last-refresh", SavedKind.STAND, "2026"),
                ),
            )

            useCase().test {
                contentRepository.emitStatus(ready())

                // A tile that promises two and opens onto one is worse than a tile that says one.
                assertEquals(1, awaitItem().wishlistCount)
            }
        }

    @Test
    fun invoke_savingWhileTheScreenIsOpen_reEmitsWithoutTheContentMoving() =
        runTest {
            useCase().test {
                contentRepository.emitStatus(ready())
                assertTrue(awaitItem().days.isEmpty())

                planRepository.toggle(savedSlot("2026:dj-alf-fri"))

                assertEquals(listOf("2026:fri"), awaitItem().days.map { it.id })
            }
        }

    private fun savedSlot(id: String) = SavedItem(id = id, kind = SavedKind.SLOT, editionId = "2026")

    private fun ready(): ContentStatus.Ready {
        val djAlf = artist("dj-alf", "DJ ALF")
        val caesure = artist("caesure", "Caesure")
        val yoga =
            Happening.Activity(
                id = "yoga",
                name = "Yoga au coucher",
                category = TERRE,
                description = null,
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                genres = emptyList(),
                price = null,
                bookingRequired = false,
                bookingUrl = null,
                equipmentProvided = null,
                suitability = null,
                supervised = null,
            )
        val silentParty = artist("silent-party", "Silent Party")

        val stand =
            Happening.Stand(
                id = "vegan-fabrik",
                name = "Vegan Fabrik",
                category = FOOD,
                description = null,
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                offering = "Cuisine végétale",
                links = emptyList(),
                menu = emptyList(),
            )

        return ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival =
                        Festival(
                            name = "Yadlo",
                            tagline = "Trois jours au bord du lac",
                            website = "https://www.yadlo.ch/",
                            currentEditionId = "2026",
                            minSupportedAppVersion = null,
                            social = emptyList(),
                        ),
                    edition =
                        Edition(
                            id = "2026",
                            year = 2026,
                            name = "Yadlo 2026",
                            venue =
                                Venue(
                                    name = "Plage de Préverenges",
                                    address = "Préverenges",
                                    latitude = 46.5,
                                    longitude = 6.5,
                                    provenance = Provenance.CONFIRMED,
                                ),
                            // Deliberately out of order, so the day sort is exercised.
                            days = listOf(SATURDAY, FRIDAY),
                            categories = listOf(MUSIQUE, TERRE, FOOD),
                            happenings = listOf(djAlf, caesure, yoga, silentParty, stand),
                            slots =
                                listOf(
                                    slot("2026:silent-party-sat", silentParty, SATURDAY, "22:00", "2026-07-12T02:00:00+02:00"),
                                    slot("2026:dj-alf-fri", djAlf, FRIDAY, "17:00", "2026-07-10T18:30:00+02:00"),
                                    slot("2026:yoga-sat", yoga, SATURDAY, "20:30", "2026-07-11T22:00:00+02:00"),
                                    slot("2026:caesure-sat", caesure, SATURDAY, "20:30", "2026-07-11T21:30:00+02:00"),
                                    // Never saved by any test, and there to be ignored by the join
                                    // and counted by the window: the beach is public, so the
                                    // morning yoga runs before the site opens at 12:00.
                                    slot("2026:sunrise-sat", yoga, SATURDAY, "10:00", "2026-07-11T11:00:00+02:00"),
                                ),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
        )
    }

    private fun artist(
        id: String,
        name: String,
    ) = Happening.Artist(
        id = id,
        name = name,
        category = MUSIQUE,
        description = null,
        images = emptyList(),
        provenance = Provenance.CONFIRMED,
        genres = emptyList(),
        links = emptyList(),
    )

    private fun slot(
        id: String,
        happening: Happening,
        day: FestivalDay,
        startTime: String,
        end: String,
    ) = Slot(
        id = id,
        happening = happening,
        day = day,
        start = Instant.parse("${day.date}T$startTime:00+02:00"),
        end = Instant.parse(end),
        provenance = Provenance.CONFIRMED,
    )

    private companion object {
        val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)
        val TERRE = Category(id = "terre", name = "Sur terre", order = 2)
        val FOOD = Category(id = "restauration", name = "Restauration", order = 3)

        val FRIDAY =
            FestivalDay(
                id = "2026:fri",
                name = "Vendredi",
                date = "2026-07-10",
                start = Instant.parse("2026-07-10T16:00:00+02:00"),
                end = Instant.parse("2026-07-11T02:00:00+02:00"),
                provenance = Provenance.CONFIRMED,
            )

        val SATURDAY =
            FestivalDay(
                id = "2026:sat",
                name = "Samedi",
                date = "2026-07-11",
                start = Instant.parse("2026-07-11T12:00:00+02:00"),
                end = Instant.parse("2026-07-12T02:00:00+02:00"),
                provenance = Provenance.CONFIRMED,
            )
    }
}
