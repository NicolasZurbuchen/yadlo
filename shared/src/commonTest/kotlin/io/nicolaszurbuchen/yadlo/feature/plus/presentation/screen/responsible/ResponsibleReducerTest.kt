package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ResponsiblePage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResponsibleReducerTest {
    private val reducer = ResponsibleStoreFactory.ReducerImpl

    @Test
    fun pageUpdated_beforeAnyEmission_thereIsNoPage() {
        assertNull(ResponsibleState().page)
    }

    @Test
    fun pageUpdated_firstEmission_holdsTheSections() {
        val page = ResponsiblePage(sections = listOf(section("a")))

        val result = with(reducer) { ResponsibleState().reduce(ResponsibleMessage.PageUpdated(page)) }

        assertEquals(listOf("a"), result.page?.sections?.map { it.id })
    }

    @Test
    fun pageUpdated_aRefreshEmptyingTheCharters_leavesAPageWithNoSections() {
        val state = ResponsibleState(page = ResponsiblePage(sections = listOf(section("a"))))

        val result = with(reducer) { state.reduce(ResponsibleMessage.PageUpdated(ResponsiblePage(sections = emptyList()))) }

        // Emptied rather than back to loading: the page landed, it simply has nothing on it, and the
        // screen has a sentence for that.
        assertEquals(emptyList(), result.page?.sections)
    }

    private fun section(id: String) = ResponsiblePage.Section(id = id, title = "Charte", body = "…", links = emptyList())
}
