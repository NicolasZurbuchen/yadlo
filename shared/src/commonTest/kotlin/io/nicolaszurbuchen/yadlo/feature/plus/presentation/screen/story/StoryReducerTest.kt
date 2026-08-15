package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StoryPage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoryReducerTest {
    private val reducer = StoryStoreFactory.ReducerImpl

    @Test
    fun storyUpdated_beforeAnyEmission_hasNotLoaded() {
        assertFalse(StoryState().hasLoaded)
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

        assertTrue(result.hasLoaded)
        assertEquals(2015, result.page?.foundedYear)
    }

    @Test
    fun storyUpdated_aNullStory_isLoadedRatherThanStillWaiting() {
        val result = with(reducer) { StoryState().reduce(StoryMessage.StoryUpdated(null)) }

        assertTrue(result.hasLoaded)
        assertNull(result.page)
    }
}
