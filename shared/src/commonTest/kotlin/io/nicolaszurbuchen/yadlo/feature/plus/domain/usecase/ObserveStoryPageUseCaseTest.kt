package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Story
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObserveStoryPageUseCaseTest {
    @Test
    fun invoke_noStoryPublished_isNull() =
        runTest {
            assertNull(pageFrom(FakeContentRepository().apply { emitStatus(ready()) }))
        }

    @Test
    fun invoke_carriesTheOriginAndItsPassage() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withStory())) }

            val page = pageFrom(repository)

            assertEquals(2015, page?.foundedYear)
            assertEquals("Une journée à Yadlo", page?.passageTitle)
        }

    @Test
    fun invoke_aStoryWithNoPassage_keepsTheOrigin() =
        runTest {
            val story = Story(foundedYear = 2015, body = "…", passage = null, provenance = Provenance.CONFIRMED)
            val repository =
                FakeContentRepository().apply { emitStatus(ready(festival = festival { copy(story = story) })) }

            assertEquals(2015, pageFrom(repository)?.foundedYear)
            assertNull(pageFrom(repository)?.passageTitle)
        }

    @Test
    fun invoke_joinsTheEditionsFiguresOntoTheLiveTruthStory() =
        runTest {
            // Two files on one screen: the origin is the same in every year's telling, the numbers
            // belong to one edition and travel with its archive.
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = withStory(), figures = listOf(figure("visiteurs", "6000"))))
                }

            assertEquals(listOf("6000"), pageFrom(repository)?.figures?.map { it.value })
        }

    @Test
    fun invoke_aPastEditionsFigures_areFlaggedSoTheScreenCanSaySo() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival = withStory(),
                            figures = listOf(figure("visiteurs", "6000", Provenance.ARCHIVED)),
                        ),
                    )
                }

            // Showing 2024's numbers under a 2026 heading without saying so would be the most
            // quietly wrong thing in the app.
            assertFalse(pageFrom(repository)?.figuresAreConfirmed == true)
        }

    @Test
    fun invoke_noFiguresAtAll_isNotACaveat() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withStory())) }

            val page = assertNotNull(pageFrom(repository))

            assertTrue(page.figures.isEmpty())
            // `all` on an empty list is true, and that is the right answer: there is nothing to
            // caveat rather than something unconfirmed.
            assertTrue(page.figuresAreConfirmed)
        }

    private suspend fun pageFrom(repository: FakeContentRepository) = ObserveStoryPageUseCase(repository)().first()

    private fun withStory() =
        festival {
            copy(
                story =
                    Story(
                        foundedYear = 2015,
                        body = "Yadlo est né en 2015.",
                        passage =
                            Story.Passage(
                                title = "Une journée à Yadlo",
                                body = "Tôt le matin.",
                                provenance = Provenance.UNVERIFIED,
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }
}
