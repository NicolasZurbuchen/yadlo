package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandKind
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
    fun invoke_aStandsOwnMarks_areNotWidenedByItsMenu() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(happenings = listOf(stand("de-lor-bokit", itemMarks = listOf("végé", "piquant")))),
                    )
                }

            // The row shows this list. Widening it would turn "sells one vegan bokit" into "is
            // vegan", which is the claim SCHEMA.md keeps the two levels apart to prevent.
            assertTrue(directoryFrom(repository).stands.single().marks.isEmpty())
        }

    @Test
    fun invoke_aStandWithNoMarkOfItsOwn_isStillMatchedByOneOfItsDishes() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(happenings = listOf(stand("de-lor-bokit", itemMarks = listOf("végé", "piquant")))),
                    )
                }

            // The filter answers "can I eat here", and one végé bokit answers it. A filter that hid
            // this stand would be wrong about the only thing it was asked.
            assertEquals(setOf("végé", "piquant"), directoryFrom(repository).stands.single().dietaryMatches)
        }

    @Test
    fun invoke_aStandMarkedThroughout_matchesOnItsOwnMarks() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(happenings = listOf(stand("vegan-fabrik", marks = listOf("végan", "bio")))))
                }

            val listing = directoryFrom(repository).stands.single()

            assertEquals(listOf("végan", "bio"), listing.marks)
            assertEquals(setOf("végan", "bio"), listing.dietaryMatches)
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
                                    stand("vegan-fabrik", marks = listOf("végan", "bio")),
                                    stand("de-lor-bokit", itemMarks = listOf("végé")),
                                    stand("guliko"),
                                ),
                        ),
                    )
                }

            // Derived rather than declared, so a chip is never offered that matches nothing and a
            // mark the content adds appears without an app release.
            assertEquals(listOf("végan", "bio", "végé"), directoryFrom(repository).marks)
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
                                    stand("vegan-fabrik", marks = listOf("végan")),
                                    stand("la-fanfrelucherie", category = CREATEURS, marks = listOf("bio")),
                                ),
                        ),
                    )
                }

            // Créateurs publishes no marks today, and the point of deriving them is that its chip
            // row is then absent rather than offering a food mark that matches nothing here.
            assertEquals(listOf("végan"), directoryFrom(repository, StandKind.FOOD).marks)
            assertEquals(listOf("bio"), directoryFrom(repository, StandKind.MAKERS).marks)
        }

    @Test
    fun invoke_aMarkOnTwoStands_isOfferedOnce() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(stand("a", marks = listOf("végé")), stand("b", marks = listOf("végé"))),
                        ),
                    )
                }

            assertEquals(listOf("végé"), directoryFrom(repository).marks)
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

    private suspend fun directoryFrom(
        repository: FakeContentRepository,
        kind: StandKind = StandKind.FOOD,
    ) = ObserveStandDirectoryUseCase(repository)(kind).first()
}
