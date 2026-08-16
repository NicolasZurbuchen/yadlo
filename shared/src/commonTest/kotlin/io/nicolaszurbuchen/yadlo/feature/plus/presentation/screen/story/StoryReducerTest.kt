package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StoryPage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StoryReducerTest {
    private val reducer = StoryStoreFactory.ReducerImpl

    @Test
    fun storyUpdated_beforeAnyEmission_carriesNoPage() {
        // The screen's whole loading state, in one field. Nothing beside it can disagree with it.
        assertNull(StoryState().page)
    }

    @Test
    fun storyUpdated_firstEmission_holdsThePage() {
        val page =
            StoryPage(
                foundedYear = 2015,
                body = "…",
                passageTitle = null,
                passageBody = null,
                figures = emptyList(),
                figuresAreConfirmed = true,
            )

        val result = with(reducer) { StoryState().reduce(StoryMessage.StoryUpdated(page)) }

        assertEquals(2015, result.page?.foundedYear)
    }
}
