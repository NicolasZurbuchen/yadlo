package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase

import app.cash.turbine.test
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveWishlistUseCaseTest {
    private val contentRepository = FakeContentRepository()
    private val planRepository = FakePlanRepository()
    private val useCase = ObserveWishlistUseCase(contentRepository, planRepository)

    @Test
    fun invoke_nothingSaved_emitsNoGroupsRatherThanEmptyOnes() =
        runTest {
            useCase().test {
                contentRepository.emitStatus(ready())

                assertTrue(awaitItem().isEmpty())
            }
        }

    @Test
    fun invoke_savedStands_areGroupedByCategoryInTheContentsOwnOrder() =
        runTest {
            planRepository.emitSaved(listOf(saved("la-fanfrelucherie"), saved("vegan-fabrik"), saved("guliko")))

            useCase().test {
                contentRepository.emitStatus(ready())

                val groups = awaitItem()

                // `restauration` is order 6 and `createurs` order 7, so restauration leads however
                // the saving happened to be ordered.
                assertEquals(listOf("restauration", "createurs"), groups.map { it.categoryId })
                assertEquals(listOf("Vegan Fabrik", "Guliko"), groups.first().stands.map { it.name })
            }
        }

    @Test
    fun invoke_aCategoryWithNothingSavedInIt_isAbsent() =
        runTest {
            planRepository.emitSaved(listOf(saved("la-fanfrelucherie")))

            useCase().test {
                contentRepository.emitStatus(ready())

                assertEquals(listOf("createurs"), awaitItem().map { it.categoryId })
            }
        }

    @Test
    fun invoke_carriesTheOfferingAndTheMarksTheRowWrites() =
        runTest {
            planRepository.emitSaved(listOf(saved("vegan-fabrik")))

            useCase().test {
                contentRepository.emitStatus(ready())

                val stand = awaitItem().single().stands.single()

                assertEquals("Cuisine végétale", stand.offering)
                assertEquals(listOf("végan", "bio"), stand.marks)
            }
        }

    @Test
    fun invoke_aSavedSlot_isNotAStandAndNeverAppearsHere() =
        runTest {
            planRepository.emitSaved(listOf(SavedItem("2026:dj-alf-fri", SavedKind.SLOT, "2026")))

            useCase().test {
                contentRepository.emitStatus(ready())

                assertTrue(awaitItem().isEmpty())
            }
        }

    @Test
    fun invoke_removingAStandWhileTheScreenIsOpen_reEmitsWithoutIt() =
        runTest {
            planRepository.emitSaved(listOf(saved("vegan-fabrik")))

            useCase().test {
                contentRepository.emitStatus(ready())
                assertEquals(1, awaitItem().size)

                // Reachable in one tap: the row opens the Stand's fiche, whose heart removes it.
                planRepository.toggle(saved("vegan-fabrik"))

                assertTrue(awaitItem().isEmpty())
            }
        }

    private fun saved(id: String) = SavedItem(id = id, kind = SavedKind.STAND, editionId = "2026")

    private fun ready(): ContentStatus.Ready {
        val veganFabrik =
            stand(id = "vegan-fabrik", name = "Vegan Fabrik", category = FOOD, offering = "Cuisine végétale")
                .copy(marks = listOf("végan", "bio"))

        return ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival =
                        Festival(
                            name = "Yadlo",
                            tagline = "Trois jours au bord du lac",
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
                            days = emptyList(),
                            // Deliberately out of order, so the category sort is exercised.
                            categories = listOf(MAKERS, FOOD, MUSIQUE),
                            happenings =
                                listOf(
                                    veganFabrik,
                                    stand(id = "guliko", name = "Guliko", category = FOOD, offering = "Cuisine géorgienne"),
                                    stand(id = "la-fanfrelucherie", name = "La Fanfrelucherie", category = MAKERS, offering = null),
                                    Happening.Artist(
                                        id = "dj-alf",
                                        name = "DJ ALF",
                                        category = MUSIQUE,
                                        description = null,
                                        images = emptyList(),
                                        provenance = Provenance.CONFIRMED,
                                        genres = emptyList(),
                                        links = emptyList(),
                                    ),
                                ),
                            slots = emptyList(),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
        )
    }

    private fun stand(
        id: String,
        name: String,
        category: Category,
        offering: String?,
    ) = Happening.Stand(
        id = id,
        name = name,
        category = category,
        description = null,
        images = emptyList(),
        provenance = Provenance.CONFIRMED,
        offering = offering,
        marks = emptyList(),
        links = emptyList(),
        menu = emptyList(),
    )

    private companion object {
        val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)
        val FOOD = Category(id = "restauration", name = "Restauration", order = 6)
        val MAKERS = Category(id = "createurs", name = "Créateurs", order = 7)
    }
}
