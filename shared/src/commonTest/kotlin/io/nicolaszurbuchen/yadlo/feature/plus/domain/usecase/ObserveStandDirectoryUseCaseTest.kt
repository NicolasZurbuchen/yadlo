package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveStandDirectoryUseCaseTest {
    @Test
    fun invoke_noStandsPublished_isEmptyRatherThanAGroupWithNothingInIt() =
        runTest {
            val directory = directoryFrom(FakeContentRepository().apply { emitStatus(ready()) })

            assertTrue(directory.groups.isEmpty())
            assertTrue(directory.marks.isEmpty())
        }

    @Test
    fun invoke_groupsByCategoryInTheDeclaredOrder() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand("la-fanfrelucherie", category = CREATEURS),
                                    stand("vegan-fabrik"),
                                ),
                        ),
                    )
                }

            // Restauration is order 6 and Créateurs is 7, so the content's order wins over the one
            // the happenings happen to be listed in.
            assertEquals(listOf("restauration", "createurs"), directoryFrom(repository).groups.map { it.categoryId })
        }

    @Test
    fun invoke_standsKeepTheOrderTheContentListsThemIn() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(happenings = listOf(stand("guliko"), stand("vegan-fabrik"))))
                }

            assertEquals(
                listOf("guliko", "vegan-fabrik"),
                directoryFrom(repository).groups.single().stands.map { it.id },
            )
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
            assertTrue(directoryFrom(repository).groups.single().stands.single().marks.isEmpty())
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
            assertEquals(
                setOf("végé", "piquant"),
                directoryFrom(repository).groups.single().stands.single().dietaryMatches,
            )
        }

    @Test
    fun invoke_aStandMarkedThroughout_matchesOnItsOwnMarks() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(happenings = listOf(stand("vegan-fabrik", marks = listOf("végan", "bio")))))
                }

            val listing = directoryFrom(repository).groups.single().stands.single()

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

            assertEquals("Cuisine géorgienne", directoryFrom(repository).groups.single().stands.single().offering)
        }

    private suspend fun directoryFrom(repository: FakeContentRepository) = ObserveStandDirectoryUseCase(repository)().first()
}
