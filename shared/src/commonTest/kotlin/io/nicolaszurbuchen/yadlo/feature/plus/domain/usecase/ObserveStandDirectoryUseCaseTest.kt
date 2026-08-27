package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.DietaryCoverage
import io.nicolaszurbuchen.yadlo.core.content.domain.model.StandKind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveStandDirectoryUseCaseTest {
    @Test
    fun invoke_noStandsPublished_isEmptyRatherThanAListWithNothingInIt() =
        runTest {
            val directory = directoryFrom(FakeContentRepository().apply { emitStatus(ready()) })

            assertTrue(directory.stands.isEmpty())
            assertTrue(directory.marks.isEmpty())
        }

    @Test
    fun invoke_returnsOnlyTheHalfThatWasAskedFor() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand("vegan-fabrik"),
                                    stand("la-fanfrelucherie", category = CREATEURS),
                                    stand("guliko"),
                                ),
                        ),
                    )
                }

            // Two entries on the tab, so the screen is never handed the other half to filter out:
            // nobody looking for dinner is also browsing for a second-hand costume.
            assertEquals(
                listOf("vegan-fabrik", "guliko"),
                directoryFrom(repository, StandKind.FOOD).stands.map { it.id },
            )
            assertEquals(
                listOf("la-fanfrelucherie"),
                directoryFrom(repository, StandKind.MAKERS).stands.map { it.id },
            )
        }

    @Test
    fun invoke_standsKeepTheOrderTheContentListsThemIn() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(happenings = listOf(stand("guliko"), stand("vegan-fabrik"))))
                }

            assertEquals(listOf("guliko", "vegan-fabrik"), directoryFrom(repository).stands.map { it.id })
        }

    @Test
    fun invoke_aMarkEveryDishCarries_coversTheWholeStand() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand(
                                        "vegan-fabrik",
                                        itemMarks = listOf(listOf("vegan", "piquant"), listOf("vegan")),
                                    ),
                                ),
                        ),
                    )
                }

            // Two different answers off one menu: everything here is vegan, one thing is hot.
            assertEquals(
                mapOf("vegan" to DietaryCoverage.ALL, "piquant" to DietaryCoverage.SOME),
                directoryFrom(repository).stands.single().dietary,
            )
        }

    @Test
    fun invoke_oneUntaggedDish_isEnoughToMakeItAnOptionRatherThanTheWholeStand() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(happenings = listOf(stand("vegan-fabrik", itemMarks = listOf(listOf("vegan"), emptyList())))),
                    )
                }

            // One forgotten drink is the difference between "100 % végan" and "options véganes",
            // and being wrong in this direction is the safe one.
            assertEquals(
                mapOf("vegan" to DietaryCoverage.SOME),
                directoryFrom(repository).stands.single().dietary,
            )
        }

    @Test
    fun invoke_aStandWithNoMenu_answersNothingRatherThanEverything() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(happenings = listOf(stand("guliko")))) }

            // Nothing published is not "all of it is vegan", and an empty menu read as ALL would
            // make every unpublished stand match every chip.
            assertTrue(directoryFrom(repository).stands.single().dietary.isEmpty())
        }

    @Test
    fun invoke_theChipSet_isEveryMarkThatMatchesSomething() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand("vegan-fabrik", itemMarks = listOf(listOf("vegan", "sans-lactose"))),
                                    stand("de-lor-bokit", itemMarks = listOf(listOf("vegetarien"), emptyList())),
                                    stand("guliko"),
                                ),
                        ),
                    )
                }

            // Derived rather than declared, so a chip is never offered that matches nothing and a
            // mark the content adds appears without an app release.
            assertEquals(
                listOf("vegan", "sans-lactose", "vegetarien"),
                directoryFrom(repository).marks,
            )
        }

    @Test
    fun invoke_theChipSet_ignoresTheOtherHalfsMarks() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand("vegan-fabrik", itemMarks = listOf(listOf("vegan"))),
                                    stand(
                                        "la-fanfrelucherie",
                                        category = CREATEURS,
                                        itemMarks = listOf(listOf("piquant")),
                                    ),
                                ),
                        ),
                    )
                }

            // Créateurs publishes no menu today, and the point of deriving the set is that its chip
            // row is then absent rather than offering a food mark that matches nothing here.
            assertEquals(listOf("vegan"), directoryFrom(repository, StandKind.FOOD).marks)
            assertEquals(listOf("piquant"), directoryFrom(repository, StandKind.MAKERS).marks)
        }

    @Test
    fun invoke_aMarkOnTwoStands_isOfferedOnce() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand("a", itemMarks = listOf(listOf("vegetarien"))),
                                    stand("b", itemMarks = listOf(listOf("vegetarien"))),
                                ),
                        ),
                    )
                }

            assertEquals(listOf("vegetarien"), directoryFrom(repository).marks)
        }

    @Test
    fun invoke_offering_isCarriedBecauseItIsWhatTheRowIsScannedFor() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(happenings = listOf(stand("guliko", offering = "Cuisine géorgienne"))))
                }

            assertEquals("Cuisine géorgienne", directoryFrom(repository).stands.single().offering)
        }

    @Test
    fun invoke_theFirstPhotograph_isCarriedBecauseItIsWhatTheCardIsMostlyMadeOf() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(happenings = listOf(stand("guliko", image = "https://example.test/guliko.webp"))),
                    )
                }

            assertEquals("https://example.test/guliko.webp", directoryFrom(repository).stands.single().imageUrl)
        }

    @Test
    fun invoke_aStandWithNoPhotograph_isNullRatherThanDropped() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(happenings = listOf(stand("guliko")))) }

            assertEquals(null, directoryFrom(repository).stands.single().imageUrl)
        }

    private suspend fun directoryFrom(
        repository: FakeContentRepository,
        kind: StandKind = StandKind.FOOD,
    ) = ObserveStandDirectoryUseCase(repository)(kind).first()
}
