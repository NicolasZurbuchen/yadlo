package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ResponsiblePage
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.responsible_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResponsibleUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoading() {
        assertTrue(ResponsibleState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_thePageArrives_stopsLoading() {
        assertFalse(loaded().isLoading)
    }

    @Test
    fun toUiModel_aPageWithNoCharters_saysSo() {
        val model = ResponsibleState(page = ResponsiblePage(sections = emptyList())).toUiModel()

        assertEquals(UiText.Resource(Res.string.responsible_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_aCharter_keepsItsHeadingBodyAndLinks() {
        val section = loaded().sections.single()

        assertEquals("FestiPlus", section.title)
        assertEquals("Une charte vaudoise.", section.body)
        assertEquals("https://festiplus.ch/", section.links.single().url)
    }

    @Test
    fun toUiModel_withSections_saysNothingAboutBeingEmpty() {
        assertNull(loaded().emptyMessage)
    }

    private fun loaded() =
        ResponsibleState(
            page =
                ResponsiblePage(
                    sections =
                        listOf(
                            ResponsiblePage.Section(
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
                        ),
                ),
        ).toUiModel()
}
