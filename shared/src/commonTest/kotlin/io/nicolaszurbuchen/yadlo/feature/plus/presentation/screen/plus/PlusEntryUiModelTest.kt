package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The one thing about the root list a compiler cannot hold.
 *
 * `PlusRoute` sends two named entries to the store and everything else to the navigator through an
 * `else`. That `else` is what makes the direct-to-navigator split cheap, and it is also the one way
 * the split can go wrong: a row added later with a leaving mark and no case beside it would open a
 * screen instead of a browser, silently, with nothing red anywhere.
 */
class PlusEntryUiModelTest {
    @Test
    fun entries_thatLeaveTheApp_areExactlyTheOnesTheRouteSendsToTheStore() {
        val leaving = PlusEntryUiModel.entries.filter { it.mark != YadloLinkMarkUiModel.DISCLOSURE }

        // If this fails, add the new entry to the `when` in PlusRoute before changing the list.
        assertEquals(listOf(PlusEntryUiModel.NEWSLETTER, PlusEntryUiModel.REPORT), leaving)
    }

    @Test
    fun entries_thatStayInTheApp_areAllHandledByTheNavKeyHandler() {
        // The handler's `when` is exhaustive over the enum, so the compiler already holds this half
        // — but only as long as no entry is quietly given a leaving mark to escape it.
        val staying = PlusEntryUiModel.entries.filter { it.mark == YadloLinkMarkUiModel.DISCLOSURE }

        assertTrue(PlusEntryUiModel.VOLUNTEERING in staying)
        assertTrue(PlusEntryUiModel.CONTACT in staying)
        assertEquals(PlusEntryUiModel.entries.size - 2, staying.size)
    }

    @Test
    fun theChevron_isNotAnnounced_andTheOtherTwoAre() {
        // A chevron on a row that is already clickable says nothing the row has not said, and
        // reading it aloud lengthens every entry in the tab. The two that leave the app are the
        // opposite case: the mark is the only thing conveying that they do.
        assertEquals(null, YadloLinkMarkUiModel.DISCLOSURE.contentDescription)
        assertTrue(YadloLinkMarkUiModel.EXTERNAL.contentDescription != null)
        assertTrue(YadloLinkMarkUiModel.MAIL.contentDescription != null)
    }
}
