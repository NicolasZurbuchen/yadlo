package io.nicolaszurbuchen.yadlo.infra.platform

import kotlin.time.Instant

/**
 * One notification, fully resolved, ready for whichever scheduler the platform has.
 *
 * **The prose is baked in here rather than built when it fires**, and that is a deliberate
 * alignment: iOS has no choice — `UNUserNotificationCenter` takes the text at schedule time — so
 * doing the same on Android is what keeps one behaviour instead of two. It also means the Android
 * receiver needs no string resources, no Koin and no content bundle at the moment an alarm goes off,
 * which is the moment least likely to have any of them.
 *
 * [staleAfter] is when this stops being worth reading. Android is told at schedule time and dismisses
 * it itself; iOS carries it in `userInfo` and sweeps on next launch, because it has nothing better.
 */
data class ScheduledNotification(
    val id: String,
    val at: Instant,
    val title: String,
    val body: String,
    val target: NotificationTarget,
    val staleAfter: Instant?,
)
