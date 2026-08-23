package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationsReducerTest {
    private val reducer = NotificationsStoreFactory.ReducerImpl

    @Test
    fun beforeAnything_neitherHalfIsKnown() {
        // Both null rather than both false, so the switch can wait rather than draw itself off and
        // then flick on a frame later.
        assertNull(NotificationsState().isEnabled)
        assertNull(NotificationsState().isPermissionGranted)
    }

    @Test
    fun enabledUpdated_holdsTheVisitorsAnswer() {
        val result = with(reducer) { NotificationsState().reduce(NotificationsMessage.EnabledUpdated(false)) }

        assertEquals(false, result.isEnabled)
    }

    @Test
    fun permissionUpdated_holdsTheSystemsAnswer() {
        val result = with(reducer) { NotificationsState().reduce(NotificationsMessage.PermissionUpdated(true)) }

        assertTrue(result.isPermissionGranted == true)
    }

    @Test
    fun eachHalfLeavesTheOtherAlone() {
        // The bug this guards is the screen's whole subject: a permission answer that reset the
        // stored preference would silently undo a tap the visitor had just made.
        val enabled = with(reducer) { NotificationsState().reduce(NotificationsMessage.EnabledUpdated(true)) }
        val both = with(reducer) { enabled.reduce(NotificationsMessage.PermissionUpdated(false)) }

        assertEquals(true, both.isEnabled)
        assertEquals(false, both.isPermissionGranted)
    }
}
