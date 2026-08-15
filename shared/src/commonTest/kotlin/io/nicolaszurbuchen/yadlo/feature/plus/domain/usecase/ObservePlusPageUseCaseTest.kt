package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Charter
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPageId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservePlusPageUseCaseTest {
    @Test
    fun invoke_responsible_makesOneSectionPerCharter() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                festival {
                                    copy(charters = listOf(charter("festiplus", "FestiPlus"), charter("autre", "Autre")))
                                },
                        ),
                    )
                }

            // Each is a separate commitment with its own body and its own site, so each is its own
            // section rather than a paragraph in a shared one.
            assertEquals(listOf("festiplus", "autre"), pageFrom(repository, PlusPageId.RESPONSIBLE).sections.map { it.id })
        }

    @Test
    fun invoke_aCharter_carriesItsNameAsTheHeadingAndItsSiteAsALink() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = festival { copy(charters = listOf(charter("festiplus", "FestiPlus"))) }))
                }

            val section = pageFrom(repository, PlusPageId.RESPONSIBLE).sections.single()

            assertEquals("FestiPlus", section.title)
            assertEquals("https://festiplus.ch/", section.links.single().url)
        }

    @Test
    fun invoke_aCharterWithNoSite_stillReadsAsASection() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(festival = festival { copy(charters = listOf(charter("interne", "Charte interne", url = null))) }),
                    )
                }

            val section = pageFrom(repository, PlusPageId.RESPONSIBLE).sections.single()

            assertTrue(section.links.isEmpty())
            assertEquals("Charte interne", section.title)
        }

    @Test
    fun invoke_noCharters_isAPageWithNoSections() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready()) }

            assertTrue(pageFrom(repository, PlusPageId.RESPONSIBLE).sections.isEmpty())
        }

    @Test
    fun invoke_social_isOneUntitledSectionOfLinks() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            festival =
                                festival {
                                    copy(
                                        social =
                                            listOf(
                                                SocialLink(id = "instagram", name = "Instagram", url = "https://a"),
                                                SocialLink(id = "tiktok", name = "TikTok", url = "https://b"),
                                            ),
                                    )
                                },
                        ),
                    )
                }

            val section = pageFrom(repository, PlusPageId.SOCIAL).sections.single()

            // The page's own title already says what these are; a second heading repeating it is
            // noise, which is why a section's title is nullable at all.
            assertNull(section.title)
            assertEquals(listOf("Instagram", "TikTok"), section.links.map { it.label })
        }

    @Test
    fun invoke_socialWithNoNetworks_isAPageWithNoSections() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready()) }

            // Not one empty section: an untitled section with no links would draw as a blank gap.
            assertTrue(pageFrom(repository, PlusPageId.SOCIAL).sections.isEmpty())
        }

    private suspend fun pageFrom(
        repository: FakeContentRepository,
        pageId: PlusPageId,
    ) = ObservePlusPageUseCase(repository)(pageId).first()

    private fun charter(
        id: String,
        name: String,
        url: String? = "https://festiplus.ch/",
    ) = Charter(id = id, name = name, body = "Une charte.", url = url, provenance = Provenance.CONFIRMED)
}
