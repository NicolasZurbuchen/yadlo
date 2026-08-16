package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PageReducerTest {
    private val reducer = PageStoreFactory.ReducerImpl

    @Test
    fun pageUpdated_beforeAnyEmission_thereIsNoPageButThereIsAKind() {
        val state = PageState(kind = PageKindUiModel.RESPONSIBLE)

        // Which page this is arrives with the destination, not with the content, so the title is
        // known before anything has been read.
        assertNull(state.page)
        assertEquals(PageKindUiModel.RESPONSIBLE, state.kind)
    }

    @Test
    fun pageUpdated_firstEmission_holdsTheSections() {
        val page = PlusPage(sections = listOf(PlusPage.Section(id = "a", title = null, body = "…", links = emptyList())))

        val result = with(reducer) { PageState(kind = PageKindUiModel.RESPONSIBLE).reduce(PageMessage.PageUpdated(page)) }

        assertEquals(listOf("a"), result.page?.sections?.map { it.id })
    }

    @Test
    fun pageUpdated_aRefreshEmptyingTheSection_doesNotChangeWhichPageThisIs() {
        val state =
            PageState(
                kind = PageKindUiModel.RESPONSIBLE,
                page = PlusPage(sections = listOf(PlusPage.Section(id = "a", title = null, body = null, links = emptyList()))),
            )

        val result = with(reducer) { state.reduce(PageMessage.PageUpdated(PlusPage(sections = emptyList()))) }

        assertEquals(PageKindUiModel.RESPONSIBLE, result.kind)
        assertEquals(emptyList(), result.page?.sections)
    }
}
