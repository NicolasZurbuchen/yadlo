package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationsUiMapperTest {
    @Test
    fun toUiModel_beforeEitherHalfIsKnown_isLoading() {
        assertTrue(NotificationsState().toUiModel().isLoading)
        assertTrue(NotificationsState(isEnabled = true).toUiModel().isLoading)
        assertTrue(NotificationsState(isPermissionGranted = true).toUiModel().isLoading)
    }

    @Test
    fun toUiModel_wantedAndAllowed_isOn() {
        val model = state(enabled = true, granted = true).toUiModel()

        assertTrue(model.isEnabled)
        assertFalse(model.isBlockedBySystem)
    }

    @Test
    fun toUiModel_wantedButRefusedBySystem_showsOffAndSaysWhy() {
        // The case the screen exists for. A switch sitting at on while the system drops everything
        // the app posts is a control that lies, so it reads off — and the line underneath is the
        // only thing that makes that honest rather than merely broken.
        val model = state(enabled = true, granted = false).toUiModel()

        assertFalse(model.isEnabled)
        assertTrue(model.isBlockedBySystem)
    }

    @Test
    fun toUiModel_turnedOffByTheVisitor_saysNothingAboutTheSystem() {
        // Nothing is blocking anything. Telling somebody their phone is refusing notifications they
        // themselves declined would be both wrong and faintly accusatory.
        val model = state(enabled = false, granted = true).toUiModel()

        assertFalse(model.isEnabled)
        assertFalse(model.isBlockedBySystem)
    }

    @Test
    fun toUiModel_turnedOffAndAlsoRefused_isJustOff() {
        val model = state(enabled = false, granted = false).toUiModel()

        assertFalse(model.isEnabled)
        assertFalse(model.isBlockedBySystem)
    }

    @Test
    fun toUiModel_theSwitchIsTheConjunction_neverJustTheStoredAnswer() {
        val positions =
            listOf(true, false).flatMap { enabled ->
                listOf(true, false).map { granted -> Triple(enabled, granted, state(enabled, granted).toUiModel().isEnabled) }
            }

        assertEquals(
            listOf(
                Triple(true, true, true),
                Triple(true, false, false),
                Triple(false, true, false),
                Triple(false, false, false),
            ),
            positions,
        )
    }

    private fun state(
        enabled: Boolean,
        granted: Boolean,
    ) = NotificationsState(isEnabled = enabled, isPermissionGranted = granted)
}
