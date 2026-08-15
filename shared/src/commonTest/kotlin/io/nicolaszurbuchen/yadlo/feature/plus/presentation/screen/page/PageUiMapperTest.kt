package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPage
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.page_empty
import yadlo.shared.generated.resources.plus_entry_responsible
import yadlo.shared.generated.resources.plus_entry_social
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PageUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoadingButAlreadyKnowsItsTitle() {
        val model = PageState(kind = PageKind.RESPONSIBLE).toUiModel()

        // The title comes from which entry was tapped, not from the content, so the bar reads
        // correctly while the body is still arriving.
        assertTrue(model.isLoading)
        assertEquals(UiText.Resource(Res.string.plus_entry_responsible), model.title)
    }

    @Test
    fun toUiModel_theTitle_matchesTheRowThatOpenedIt() {
        val model = PageState(kind = PageKind.SOCIAL, page = PlusPage(sections = emptyList())).toUiModel()

        assertEquals(UiText.Resource(Res.string.plus_entry_social), model.title)
    }

    @Test
    fun toUiModel_aPublishedSectionWithNoSections_saysSo() {
        val model = PageState(kind = PageKind.RESPONSIBLE, page = PlusPage(sections = emptyList())).toUiModel()

        assertEquals(UiText.Resource(Res.string.page_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_aTitledSection_keepsItsHeadingBodyAndLinks() {
        val section = loaded().sections.first()

        assertEquals("FestiPlus", section.title)
        assertEquals("Une charte vaudoise.", section.body)
        assertEquals("https://festiplus.ch/", section.links.single().url)
    }

    @Test
    fun toUiModel_anUntitledSection_staysUntitled() {
        val section = loaded().sections.last()

        // The page's own title already said what these are; a second heading repeating it is noise,
        // which is the whole reason a section's title is nullable.
        assertNull(section.title)
        assertEquals(listOf("Instagram"), section.links.map { it.label })
    }

    @Test
    fun toUiModel_withSections_saysNothingAboutBeingEmpty() {
        assertNull(loaded().emptyMessage)
    }

    private fun loaded() =
        PageState(
            kind = PageKind.RESPONSIBLE,
            page =
                PlusPage(
                    sections =
                        listOf(
                            PlusPage.Section(
                                id = "festiplus",
                                title = "FestiPlus",
                                body = "Une charte vaudoise.",
                                links =
                                    listOf(
                                        InfoLink(
                                            id = "festiplus",
                                            label = "FestiPlus",
                                            sublabel = null,
                                            url = "https://festiplus.ch/",
                                        ),
                                    ),
                            ),
                            PlusPage.Section(
                                id = "reseaux",
                                title = null,
                                body = null,
                                links =
                                    listOf(
                                        InfoLink(
                                            id = "instagram",
                                            label = "Instagram",
                                            sublabel = null,
                                            url = "https://example.ch/",
                                        ),
                                    ),
                            ),
                        ),
                ),
        ).toUiModel()
}
