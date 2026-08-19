package io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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

    private fun ready(
        slots: List<Slot>,
        days: List<FestivalDay> = listOf(friday(), saturday(), sunday()),
    ) = ContentStatus.Ready(
        bundle =
            ContentBundle(
                festival =
                    Festival(
                        name = "Yadlo",
                        tagline = "Mouille ton corps, arrose ton esprit",
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
                        happenings = emptyList(),
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
                    images = emptyList(),
                    provenance = Provenance.CONFIRMED,
                    genres = emptyList(),
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
