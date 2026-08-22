package io.nicolaszurbuchen.yadlo.infra.platform

/**
 * Where tapping a notification lands.
 *
 * It encodes to a string because it has to survive a trip through the operating system — an Android
 * `Intent` extra, an iOS `userInfo` dictionary — and come back on a process that may have been dead
 * when the notification fired. A string keeps that crossing legible in a bug report and keeps the
 * two platforms carrying the identical payload.
 *
 * **A custom scheme would be the other way to do this, and here it is the right one** — the mirror
 * image of DECISIONS.md § *A share is plain text*. That decision refused `yadlo://` for shares
 * because a share is precisely the case where the recipient does not have the app. This payload
 * never leaves the device, so the objection does not apply.
 */
sealed class NotificationTarget {
    data object Home : NotificationTarget()

    data object Programme : NotificationTarget()

    /** The fiche. A Slot has no screen of its own, so a Slot reminder points at its Happening. */
    data class Happening(
        val id: String,
    ) : NotificationTarget()

    fun encode(): String =
        when (this) {
            Home -> HOME
            Programme -> PROGRAMME
            is Happening -> "$HAPPENING$id"
        }

    companion object {
        /**
         * Null for anything unrecognised, which is a reachable state rather than a hedge: a
         * notification scheduled by one version of the app can be tapped after an update to
         * another. Opening the app on its usual start tab is the right answer to a target that no
         * longer means anything.
         */
        fun decode(raw: String): NotificationTarget? =
            when {
                raw == HOME -> Home
                raw == PROGRAMME -> Programme
                raw.startsWith(HAPPENING) -> raw.removePrefix(HAPPENING).takeIf { it.isNotEmpty() }?.let(::Happening)
                else -> null
            }

        private const val HOME = "home"
        private const val PROGRAMME = "programme"
        private const val HAPPENING = "happening:"
    }
}
