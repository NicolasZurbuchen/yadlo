package io.nicolaszurbuchen.yadlo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.nicolaszurbuchen.yadlo.app.notification.ReminderScheduler
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

/**
 * Puts the reminders back after a restart.
 *
 * **Android forgets every alarm on reboot** — the system keeps no record of them across a restart,
 * so a Slot hearted on the Thursday and a phone restarted on the Friday morning means a reminder
 * that silently never arrives. Every other path back into the app reschedules on its own; this is
 * the only case where nothing brings the app to the front in time.
 *
 * **It has to load the content before it can do anything**, which is the part that makes this more
 * than a one-line receiver. The process here is fresh: `YadloApplication` has started Koin, but the
 * content repository is still on `Loading`, and a scheduler asked to plan against nothing plans
 * nothing. `refresh()` publishes the cached bundle before it touches the network, so waiting for
 * `Ready` is a disk read rather than a fetch, and the reboot of a phone with no signal still works.
 *
 * The refresh runs in a scope of its own so waiting on it is not waiting on the network leg that
 * follows. If nothing was ever cached — a fresh install, never opened, then rebooted — nothing
 * becomes ready inside the timeout and there was nothing to reschedule anyway.
 *
 * **A caveat worth knowing rather than fixing**: several OEMs suppress `BOOT_COMPLETED` for apps the
 * visitor has not opened recently. This is the correct thing to register and it is not a guarantee.
 */
class BootReceiver :
    BroadcastReceiver(),
    // KoinComponent rather than koin-android's by inject(), which is an extension on
    // ComponentCallbacks — an Application, an Activity, a Service. A BroadcastReceiver is none of
    // those, so it resolves from the default context by hand.
    KoinComponent {
    private val contentRepository: ContentRepository by inject()
    private val reminderScheduler: ReminderScheduler by inject()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // Without this the process is eligible to die the moment onReceive returns, which is long
        // before any of the below has finished.
        val pending = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        scope.launch { contentRepository.refresh() }

        scope.launch {
            try {
                withTimeoutOrNull(BOOT_WORK_TIMEOUT) {
                    contentRepository.observeStatus().first { it is ContentStatus.Ready }
                    reminderScheduler.sync()
                }
            } finally {
                pending.finish()
            }
        }
    }

    private companion object {
        /**
         * Generous, because a boot is the slowest moment in a device's life and the work is a disk
         * read the visitor never sees. Short enough that a device with nothing cached is not held
         * awake waiting for a network that may not exist yet.
         */
        val BOOT_WORK_TIMEOUT = 20.seconds
    }
}
