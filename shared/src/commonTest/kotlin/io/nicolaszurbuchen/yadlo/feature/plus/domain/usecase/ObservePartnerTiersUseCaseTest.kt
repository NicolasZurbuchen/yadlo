package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservePartnerTiersUseCaseTest {
    @Test
    fun invoke_noPartnersPublished_isEmpty() =
        runTest {
            assertTrue(tiersFrom(FakeContentRepository().apply { emitStatus(ready()) }).isEmpty())
        }

    @Test
    fun invoke_sortsByTheDeclaredOrderRatherThanTheAuthoredOne() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            partners =
                                listOf(
                                    tier("cygnes-bronze", order = 4, members = listOf("a")),
                                    tier("sponsors", order = 1, members = listOf("b")),
                                    tier("cygnes-or", order = 2, members = listOf("c")),
                                ),
                        ),
                    )
                }

            // The order *is* the hierarchy, and it is the one list in the content where getting the
            // sequence wrong is visible to the people who paid for the placement.
            assertEquals(listOf("sponsors", "cygnes-or", "cygnes-bronze"), tiersFrom(repository).map { it.id })
        }

    @Test
    fun invoke_aTierWithNoMembers_isNotDrawnAsAnEmptyHeading() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            partners =
                                listOf(
                                    tier("sponsors", order = 1, members = listOf("a")),
                                    tier("cygnes-argent", order = 3, members = emptyList()),
                                ),
                        ),
                    )
                }

            assertEquals(listOf("sponsors"), tiersFrom(repository).map { it.id })
        }

    @Test
    fun invoke_membersKeepTheirNullUrl_soTheTapCanSaySo() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            partners =
                                listOf(
                                    tier(
                                        "cygnes-or",
                                        order = 2,
                                        members = listOf("edifice", "totem"),
                                        withoutSite = setOf("edifice"),
                                    ),
                                ),
                        ),
                    )
                }

            // Five of the thirty-nine have no site. Dropping the null here would leave the screen
            // unable to tell a partner without a website from one it forgot to link.
            val members = tiersFrom(repository).single().members
            assertNull(members.first().url)
            assertEquals("https://example.ch/totem", members.last().url)
        }

    private suspend fun tiersFrom(repository: FakeContentRepository) = ObservePartnerTiersUseCase(repository)().first()
}
