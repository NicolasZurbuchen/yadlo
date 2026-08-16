package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveFaqUseCaseTest {
    @Test
    fun invoke_nothingPublished_isAnEmptyListRatherThanAnAbsentSection() =
        runTest {
            assertTrue(ObserveFaqUseCase(FakeContentRepository().apply { emitStatus(ready()) })().first().isEmpty())
        }

    @Test
    fun invoke_carriesTheQuestionAndItsAnswerTogether() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withFaq())) }

            val entry = ObserveFaqUseCase(repository)().first().single()

            assertEquals("L'entrée est-elle payante ?", entry.question)
            assertEquals("Non. L'entrée est libre les trois jours.", entry.answer)
        }

    @Test
    fun invoke_keepsTheOrderTheAssociationPublishedThemIn() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                festival {
                                    copy(
                                        faq =
                                            listOf(
                                                entry("entree", "Payant ?"),
                                                entry("chiens", "Les chiens sont-ils admis ?"),
                                            ),
                                    )
                                },
                        ),
                    )
                }

            // Which question comes first is an editorial decision about what gets asked most, and
            // sorting it here would take that away.
            assertEquals(listOf("entree", "chiens"), ObserveFaqUseCase(repository)().first().map { it.id })
        }

    @Test
    fun invoke_anAnswerCorrectedMidFestival_reachesTheScreen() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withFaq())) }
            val useCase = ObserveFaqUseCase(repository)
            assertEquals(1, useCase().first().size)

            repository.emitStatus(
                ready(festival = festival { copy(faq = listOf(entry("entree", "Payant ?"), entry("wc", "Des WC ?"))) }),
            )

            // The FAQ is the screen a correction is most likely to be pushed to, which is why it
            // observes rather than reads once.
            assertEquals(2, useCase().first().size)
        }

    private fun withFaq() =
        festival {
            copy(
                faq =
                    listOf(
                        FaqEntry(
                            id = "entree",
                            question = "L'entrée est-elle payante ?",
                            answer = "Non. L'entrée est libre les trois jours.",
                            provenance = Provenance.CONFIRMED,
                        ),
                    ),
            )
        }

    private fun entry(
        id: String,
        question: String,
    ) = FaqEntry(id = id, question = question, answer = "…", provenance = Provenance.CONFIRMED)
}
