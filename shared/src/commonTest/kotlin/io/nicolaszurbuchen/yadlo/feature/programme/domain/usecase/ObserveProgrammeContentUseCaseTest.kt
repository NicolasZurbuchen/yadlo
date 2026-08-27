package io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Image
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Venue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class ObserveProgrammeContentUseCaseTest {
    private val repository = FakeContentRepository()
    private val useCase = ObserveProgrammeContentUseCase(repository)

    @Test
    fun invoke_nothingReadyYet_emitsNothing() =
        runTest {
            useCase().test {
                expectNoEvents()
                cancel()
            }
        }

    @Test
    fun invoke_bundleIsReady_resolvesEachSlotIntoTheFieldsARowShows() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside())))

                val slot = awaitItem().slots.single()

                assertEquals("2026:dubside-sat", slot.id)
                assertEquals("2026:sat", slot.dayId)
                assertEquals("dubside", slot.happeningId)
                assertEquals("Dubside", slot.name)
                assertEquals("musique", slot.categoryId)
                assertEquals("Musique", slot.categoryName)
                cancel()
            }
        }

    @Test
    fun invoke_standHasOpeningWindows_keepsThemOffTheProgramme() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside(), barOpeningHours())))

                // The bar being open from 12:00 to 02:00 is not a fourteen-hour entry on the day.
                assertEquals(listOf("2026:dubside-sat"), awaitItem().slots.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_twoSlotsStartTogether_putsTheShorterFirst() =
        runTest {
            useCase().test {
                repository.emitStatus(
                    ready(
                        slots =
                            listOf(
                                gladiaSup(),
                                unoTournament(),
                                dubside(),
                            ),
                    ),
                )

                // Time first. Then, at 14:00, the two-hour tournament before the seven-hour
                // activity it starts alongside — which is the order someone scanning for "what is
                // on at two" reads in.
                assertEquals(
                    listOf("2026:uno-sat", "2026:gladiasup-sat", "2026:dubside-sat"),
                    awaitItem().slots.map { it.id },
                )
                cancel()
            }
        }

    @Test
    fun invoke_editionDeclaresCategoriesNothingIsProgrammedUnder_leavesThemOffTheChips() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside(), unoTournament())))

                // `restauration` belongs to Stands alone, and a chip that always empties the list
                // reads as a broken filter rather than an honest empty day.
                assertEquals(listOf("musique", "terre"), awaitItem().categories.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_categoriesAreOrderedByTheContentsOwnOrder_notByTheSlotsTheyCameFrom() =
        runTest {
            useCase().test {
                // The land activity is the earlier Slot; `musique` is order 1 in the content.
                repository.emitStatus(ready(slots = listOf(unoTournament(), dubside())))

                assertEquals(listOf("musique", "terre"), awaitItem().categories.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_daysArriveOutOfOrder_comeBackChronological() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside()), days = listOf(sunday(), friday(), saturday())))

                assertEquals(listOf("2026:fri", "2026:sat", "2026:sun"), awaitItem().days.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_activityWithATariff_carriesItsPriceThrough() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(gladiaSup())))

                assertEquals(5.0, awaitItem().slots.single().price?.tiers?.single()?.amount?.amount)
                cancel()
            }
        }

    @Test
    fun invoke_artistSlot_carriesNoPriceAtAll() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside())))

                // Not "free" — an Artist is covered by getting in, and a row saying "gratuit" under
                // every concert answers a question nobody asked.
                assertNull(awaitItem().slots.single().price)
                cancel()
            }
        }

    @Test
    fun invoke_bundleIsReady_buildsTheCatalogueFromTheHappeningsRatherThanFromTheSlots() =
        runTest {
            useCase().test {
                // SUP Yoga three times on one day is three Slots and one thing to do.
                repository.emitStatus(ready(slots = listOf(supYogaAt("14:00"), supYogaAt("16:00"), supYogaAt("18:00"))))

                assertEquals(listOf("sup-yoga"), awaitItem().catalogue.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_standsArePublished_keepsThemOutOfTheCatalogue() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside(), barOpeningHours())))

                // A Stand is browsed in Plus, with this screen's own card. A second door onto the
                // same eight stalls is what "one place to browse a thing" forbids.
                assertEquals(listOf("dubside"), awaitItem().catalogue.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_artistIsInTheCatalogue_carriesThePictureAndTheGenresARowHasNoRoomFor() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside())))

                val entry = awaitItem().catalogue.single()

                assertEquals("Dubside", entry.name)
                assertEquals("Musique", entry.categoryName)
                assertEquals("Techno-house", entry.genres.single())
                assertEquals("https://example.test/dubside.webp", entry.imageUrl)
                cancel()
            }
        }

    @Test
    fun invoke_catalogueIsOrdered_byTheContentsCategoryOrderThenByName() =
        runTest {
            useCase().test {
                // `terre` is order 4 and `musique` order 1, and the land activity is the earlier
                // Slot — so a Catalogue ordered by the timetable would put it first.
                repository.emitStatus(ready(slots = listOf(unoTournament(), dubside())))

                assertEquals(listOf("dubside", "uno"), awaitItem().catalogue.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_happeningIsInTheCatalogueButHasNoSlotYet_stillOffersItsCategoryChip() =
        runTest {
            useCase().test {
                repository.emitStatus(
                    ready(
                        slots = listOf(dubside()),
                        happenings = listOf(dubside().happening, unoTournament().happening),
                    ),
                )

                // The chips filter both views, so they have to cover both. Without the union, an
                // Activity published before its hours would sit in the Catalogue with no way to
                // filter to it.
                assertEquals(listOf("musique", "terre"), awaitItem().categories.map { it.id })
                cancel()
            }
        }

    @Test
    fun invoke_editionHasSlots_saysTheProgrammeIsPublished() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(dubside())))

                // Read off the Edition rather than off the filtered list, so the three places that
                // derive a Phase derive the same one.
                assertEquals(true, awaitItem().hasPublishedProgramme)
                cancel()
            }
        }

    @Test
    fun invoke_onlyAStandPublishesHours_isNotAPublishedProgrammeToThisScreenAlone() =
        runTest {
            useCase().test {
                repository.emitStatus(ready(slots = listOf(barOpeningHours())))

                val content = awaitItem()

                // The list is empty, but the Edition has Slots — and the shell and Accueil both
                // read it that way, so this must too or the app would sit in two Phases at once.
                assertTrue(content.slots.isEmpty())
                assertEquals(true, content.hasPublishedProgramme)
                cancel()
            }
        }

    private fun supYogaAt(time: String) =
        Slot(
            id = "2026:sup-yoga-sat-$time",
            happening = activity(id = "sup-yoga", name = "SUP Yoga", price = null),
            day = saturday(),
            start = Instant.parse("2026-07-11T$time:00+02:00"),
            end = Instant.parse("2026-07-11T$time:00+02:00").plus(1.hours),
            provenance = Provenance.CONFIRMED,
        )

    /**
     * [happenings] defaults to the ones the Slots hang off, which is what a real bundle holds: the
     * remote mapper resolves every Slot against the Edition's own list, so a Slot whose Happening
     * were missing from it could not exist.
     */
    private fun ready(
        slots: List<Slot>,
        days: List<FestivalDay> = listOf(friday(), saturday(), sunday()),
        happenings: List<Happening> = slots.map { it.happening }.distinctBy { it.id },
    ) = ContentStatus.Ready(
        bundle =
            ContentBundle(
                festival =
                    Festival(
                        name = "Yadlo",
                        tagline = "Mouille ton corps, arrose ton esprit",
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
                                address = "Route de la Plage, 1028 Préverenges",
                                latitude = 46.51,
                                longitude = 6.53,
                                provenance = Provenance.CONFIRMED,
                            ),
                        days = days,
                        categories = listOf(MUSIQUE, LAND, RESTAURATION),
                        happenings = happenings,
                        slots = slots,
                        partners = emptyList(),
                        figures = emptyList(),
                    ),
                announcements = emptyList(),
            ),
        updateRequired = false,
    )

    private fun friday() =
        FestivalDay(
            id = "2026:fri",
            name = "Vendredi",
            date = "2026-07-10",
            start = Instant.parse("2026-07-10T16:00:00+02:00"),
            end = Instant.parse("2026-07-11T02:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun saturday() =
        FestivalDay(
            id = "2026:sat",
            name = "Samedi",
            date = "2026-07-11",
            start = Instant.parse("2026-07-11T12:00:00+02:00"),
            end = Instant.parse("2026-07-12T03:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun sunday() =
        FestivalDay(
            id = "2026:sun",
            name = "Dimanche",
            date = "2026-07-12",
            start = Instant.parse("2026-07-12T12:00:00+02:00"),
            end = Instant.parse("2026-07-12T22:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun dubside() =
        Slot(
            id = "2026:dubside-sat",
            happening =
                Happening.Artist(
                    id = "dubside",
                    name = "Dubside",
                    category = MUSIQUE,
                    description = null,
                    images = listOf(Image(url = "https://example.test/dubside.webp", credit = null)),
                    provenance = Provenance.CONFIRMED,
                    genres = listOf("Techno-house"),
                    links = emptyList(),
                ),
            day = saturday(),
            start = Instant.parse("2026-07-11T16:00:00+02:00"),
            end = Instant.parse("2026-07-11T18:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun unoTournament() =
        Slot(
            id = "2026:uno-sat",
            happening = activity(id = "uno", name = "Tournoi de UNO", price = null),
            day = saturday(),
            start = Instant.parse("2026-07-11T14:00:00+02:00"),
            end = Instant.parse("2026-07-11T16:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun gladiaSup() =
        Slot(
            id = "2026:gladiasup-sat",
            happening =
                activity(
                    id = "gladiasup",
                    name = "GladiaSUP",
                    price =
                        Price(
                            free = false,
                            tiers = listOf(Price.Tier(label = null, amount = Money(5.0, "CHF"), per = null)),
                            deposit = null,
                            provenance = Provenance.CONFIRMED,
                        ),
                ),
            day = saturday(),
            start = Instant.parse("2026-07-11T14:00:00+02:00"),
            end = Instant.parse("2026-07-11T19:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun barOpeningHours() =
        Slot(
            id = "2026:bar-sat",
            happening =
                Happening.Stand(
                    id = "bar",
                    name = "Le bar",
                    category = RESTAURATION,
                    description = null,
                    images = emptyList(),
                    provenance = Provenance.CONFIRMED,
                    offering = null,
                    links = emptyList(),
                    menu = emptyList(),
                ),
            day = saturday(),
            start = Instant.parse("2026-07-11T12:00:00+02:00"),
            end = Instant.parse("2026-07-12T02:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun activity(
        id: String,
        name: String,
        price: Price?,
    ) = Happening.Activity(
        id = id,
        name = name,
        category = LAND,
        description = null,
        images = emptyList(),
        provenance = Provenance.CONFIRMED,
        genres = emptyList(),
        price = price,
        bookingRequired = false,
        bookingUrl = null,
        equipmentProvided = null,
        suitability = null,
        supervised = null,
    )

    private companion object {
        val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)
        val LAND = Category(id = "terre", name = "Sur terre", order = 4)
        val RESTAURATION = Category(id = "restauration", name = "Restauration", order = 6)
    }
}
