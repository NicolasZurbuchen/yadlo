package io.nicolaszurbuchen.yadlo.feature.happening.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Link
import io.nicolaszurbuchen.yadlo.common.content.domain.model.MenuGroup
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class ObserveHappeningDetailUseCaseTest {
    private val repository = FakeContentRepository()
    private val useCase = ObserveHappeningDetailUseCase(repository)

    @Test
    fun invoke_beforeTheBundleIsReady_emitsNothingRatherThanAMissingFiche() =
        runTest {
            useCase("dj-alf").test {
                expectNoEvents()

                repository.emitStatus(ContentStatus.Loading)
                expectNoEvents()

                cancel()
            }
        }

    @Test
    fun invoke_artist_carriesItsGenresAsTagsAndItsLinks() =
        runTest {
            useCase("dj-alf").test {
                repository.emitStatus(ready())

                val detail = awaitItem()

                assertEquals("DJ ALF", detail?.name)
                assertEquals(listOf("House", "Disco"), detail?.tags)
                assertEquals(listOf("https://djalf.ch/"), detail?.links?.map { it.url })
                cancel()
            }
        }

    @Test
    fun invoke_artist_hasNoneOfTheFieldsOnlyAnActivityCarries() =
        runTest {
            useCase("dj-alf").test {
                repository.emitStatus(ready())

                val detail = awaitItem()

                assertNull(detail?.price)
                assertNull(detail?.equipmentProvided)
                assertNull(detail?.suitability)
                assertNull(detail?.supervised)
                assertEquals(false, detail?.bookingRequired)
                assertTrue(detail?.menu.orEmpty().isEmpty())
                cancel()
            }
        }

    @Test
    fun invoke_activity_carriesItsPriceAndItsBookingPage() =
        runTest {
            useCase("silent-party").test {
                repository.emitStatus(ready())

                val detail = awaitItem()

                assertEquals(2, detail?.price?.tiers?.size)
                assertEquals(50.0, detail?.price?.deposit?.amount?.amount)
                assertEquals(true, detail?.bookingRequired)
                assertEquals("https://booking.example/", detail?.bookingUrl)
                assertEquals(true, detail?.equipmentProvided)
            }
        }

    @Test
    fun invoke_activity_hasNoLinksBecauseItsOneOutwardAddressIsTheBookingPage() =
        runTest {
            useCase("silent-party").test {
                repository.emitStatus(ready())

                // Not "the content has none": the booking url is an action beside the price rather
                // than a reference in a list of them, so it never appears twice.
                assertTrue(awaitItem()?.links.orEmpty().isEmpty())
            }
        }

    @Test
    fun invoke_stand_leadsItsTagsWithTheOfferingThenTheMarksThatQualifyIt() =
        runTest {
            useCase("vegan-fabrik").test {
                repository.emitStatus(ready())

                assertEquals(listOf("Cuisine végétale", "végan", "bio"), awaitItem()?.tags)
            }
        }

    @Test
    fun invoke_stand_carriesItsMenu() =
        runTest {
            useCase("vegan-fabrik").test {
                repository.emitStatus(ready())

                val detail = awaitItem()

                assertEquals(listOf("Plats"), detail?.menu?.map { it.name })
                assertEquals(listOf("Assiette de mezzés"), detail?.menu?.first()?.items?.map { it.name })
            }
        }

    @Test
    fun invoke_slots_areOrderedAndCarryTheDayTheyWereAuthoredOn() =
        runTest {
            useCase("silent-party").test {
                repository.emitStatus(ready())

                val slots = awaitItem()?.slots.orEmpty()

                assertEquals(listOf("2026:silent-party-fri", "2026:silent-party-sat"), slots.map { it.id })
                assertEquals(listOf("Vendredi", "Samedi"), slots.map { it.dayName })
            }
        }

    @Test
    fun invoke_slotsOfOtherHappenings_areNotOnThisFiche() =
        runTest {
            useCase("dj-alf").test {
                repository.emitStatus(ready())

                assertEquals(listOf("2026:dj-alf-fri"), awaitItem()?.slots?.map { it.id })
            }
        }

    @Test
    fun invoke_anIdTheEditionDoesNotDeclare_emitsNullRatherThanFailing() =
        runTest {
            // Reachable without a bug on the app's side: a fiche survives process death, so someone
            // can restore the app onto a Happening a content refresh has since dropped.
            useCase("gone-since-last-refresh").test {
                repository.emitStatus(ready())

                assertNull(awaitItem())
            }
        }

    @Test
    fun invoke_aRefreshLands_reEmitsSoTheOpenFicheFollowsIt() =
        runTest {
            useCase("dj-alf").test {
                repository.emitStatus(ready())
                assertEquals("DJ ALF", awaitItem()?.name)

                repository.emitStatus(ready(artistName = "DJ ALF (b2b)"))
                assertEquals("DJ ALF (b2b)", awaitItem()?.name)
            }
        }

    private fun ready(artistName: String = "DJ ALF"): ContentStatus.Ready {
        val artist =
            Happening.Artist(
                id = "dj-alf",
                name = artistName,
                category = MUSIC,
                description = "Bangers only.",
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                genres = listOf("House", "Disco"),
                links = listOf(Link(type = "website", url = "https://djalf.ch/")),
            )

        val activity =
            Happening.Activity(
                id = "silent-party",
                name = "Silent Party",
                category = SILENT,
                description = "Trois ambiances, un seul dancefloor.",
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                genres = listOf("All style"),
                price =
                    Price(
                        free = false,
                        tiers =
                            listOf(
                                Price.Tier(label = "Adulte", amount = Money(25.0, "CHF"), per = null),
                                Price.Tier(label = "Moins de 16 ans", amount = Money(15.0, "CHF"), per = null),
                            ),
                        deposit = Price.Deposit(amount = Money(50.0, "CHF"), note = "Caution casque."),
                        provenance = Provenance.CONFIRMED,
                    ),
                bookingRequired = true,
                bookingUrl = "https://booking.example/",
                equipmentProvided = true,
                suitability = null,
                supervised = null,
            )

        val stand =
            Happening.Stand(
                id = "vegan-fabrik",
                name = "Vegan Fabrik",
                category = FOOD,
                description = "Cuisine 100 % végétale.",
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                offering = "Cuisine végétale",
                marks = listOf("végan", "bio"),
                links = emptyList(),
                menu =
                    listOf(
                        MenuGroup(
                            id = "plats",
                            name = "Plats",
                            description = null,
                            source = "Liste transmise",
                            items =
                                listOf(
                                    MenuGroup.Item(
                                        name = "Assiette de mezzés",
                                        price = Money(15.0, "CHF"),
                                        description = null,
                                        marks = emptyList(),
                                        provenance = Provenance.UNVERIFIED,
                                    ),
                                ),
                        ),
                    ),
            )

        return ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival = festival(),
                    edition =
                        Edition(
                            id = "2026",
                            year = 2026,
                            name = "Yadlo 2026",
                            venue = venue(),
                            days = listOf(FRIDAY, SATURDAY),
                            categories = listOf(MUSIC, SILENT, FOOD),
                            happenings = listOf(artist, activity, stand),
                            // Deliberately out of order, so the sort is exercised rather than inherited.
                            slots =
                                listOf(
                                    slot("2026:silent-party-sat", activity, SATURDAY, "2026-07-11T21:00:00+02:00"),
                                    slot("2026:dj-alf-fri", artist, FRIDAY, "2026-07-10T17:00:00+02:00"),
                                    slot("2026:silent-party-fri", activity, FRIDAY, "2026-07-10T21:00:00+02:00"),
                                ),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
        )
    }

    private fun slot(
        id: String,
        happening: Happening,
        day: FestivalDay,
        start: String,
    ) = Slot(
        id = id,
        happening = happening,
        day = day,
        start = Instant.parse(start),
        end = Instant.parse(start) + kotlin.time.Duration.parse("1h"),
        provenance = Provenance.CONFIRMED,
    )

    private fun festival() =
        Festival(
            name = "Yadlo",
            tagline = "Trois jours au bord du lac",
            currentEditionId = "2026",
            minSupportedAppVersion = null,
            social = emptyList(),
        )

    private fun venue() =
        Venue(
            name = "Plage de Préverenges",
            address = "Préverenges",
            latitude = 46.5,
            longitude = 6.5,
            provenance = Provenance.CONFIRMED,
        )

    private companion object {
        val MUSIC = Category(id = "musique", name = "Musique", order = 1)
        val SILENT = Category(id = "silent", name = "Silent Party", order = 2)
        val FOOD = Category(id = "restauration", name = "Restauration", order = 3)

        val FRIDAY =
            FestivalDay(
                id = "2026:fri",
                name = "Vendredi",
                date = "2026-07-10",
                start = Instant.parse("2026-07-10T12:00:00+02:00"),
                end = Instant.parse("2026-07-11T03:00:00+02:00"),
                provenance = Provenance.CONFIRMED,
            )

        val SATURDAY =
            FestivalDay(
                id = "2026:sat",
                name = "Samedi",
                date = "2026-07-11",
                start = Instant.parse("2026-07-11T10:00:00+02:00"),
                end = Instant.parse("2026-07-12T03:00:00+02:00"),
                provenance = Provenance.CONFIRMED,
            )
    }
}
