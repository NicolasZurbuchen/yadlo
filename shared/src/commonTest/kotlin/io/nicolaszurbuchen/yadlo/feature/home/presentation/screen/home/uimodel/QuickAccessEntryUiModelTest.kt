package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The two things about the promoted tiles a compiler cannot hold.
 *
 * `HomeRoute` splits on whether a tile carries a url — one that does goes to the browser, one that
 * does not goes to the navigator — while the *mark* is what tells the reader which of the two is
 * about to happen. Nothing in the type system ties those together, so an entry could be given a
 * leaving mark and no url, or the reverse, and the only symptom would be a tile that quietly does
 * the wrong thing.
 *
 * The mirror of `PlusEntryUiModelTest`, guarding the same seam on the other tab.
 */
class QuickAccessEntryUiModelTest {
    @Test
    fun entries_thatLeaveTheApp_areExactlyTheOneTheMapperGivesAUrl() {
        val leaving = QuickAccessEntryUiModel.entries.filter { it.mark != YadloLinkMarkUiModel.DISCLOSURE }

        // If this fails, `HomeUiMapper` must give the new entry a url and `HomeNavKeyHandler` must
        // stop claiming to have a destination for it — in that order.
        assertEquals(listOf(QuickAccessEntryUiModel.NEWSLETTER), leaving)
    }

    @Test
    fun entries_thatStayInTheApp_areTheFiveTheNavKeyHandlerCanReach() {
        // The handler's `when` is exhaustive over the enum, so the compiler already holds this half
        // — but only as long as no entry is quietly given a leaving mark to escape it.
        val staying = QuickAccessEntryUiModel.entries.filter { it.mark == YadloLinkMarkUiModel.DISCLOSURE }

        assertEquals(
            listOf(
                QuickAccessEntryUiModel.PAYMENT,
                QuickAccessEntryUiModel.ACCESS,
                QuickAccessEntryUiModel.VOLUNTEERING,
                QuickAccessEntryUiModel.CONTACT,
                QuickAccessEntryUiModel.STORY,
            ),
            staying,
        )
    }

    @Test
    fun entries_areAShortlistRatherThanASecondCopyOfThePlusTab() {
        // The number itself is the guard. Plus holds sixteen rows; if this drifts upward the block
        // has stopped being a promotion and become a smaller table of contents, which is the one
        // way it ends up worth less than the tap it saves.
        assertTrue(
            QuickAccessEntryUiModel.entries.size <= MOST_A_PHASE_MAY_PROMOTE * PHASES_THAT_PROMOTE,
            "${QuickAccessEntryUiModel.entries.size} promoted entries",
        )
    }

    private companion object {
        /** Three rows is the most any one phase promotes, and OFF_SEASON is the phase that does. */
        const val MOST_A_PHASE_MAY_PROMOTE = 3

        /** Every phase but LIVE, which promotes nothing. */
        const val PHASES_THAT_PROMOTE = 4
    }
}
