package io.nicolaszurbuchen.yadlo.app.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.infra.platform.rememberNotificationPermissionRequester
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Keeps what the operating system has scheduled in step with what is saved, and asks for permission
 * at the one moment it can be justified.
 *
 * **Permission is requested when the first Slot is hearted, never on launch.** Asked at startup, the
 * prompt is an app the visitor has not used yet asking to interrupt them, and most people say no —
 * which also loses the reminders they would have wanted, because both platforms treat a refusal as
 * final. Asked here it is a prompt about the thing they just did. There is one attempt to spend and
 * this is where it buys the most.
 *
 * Neither platform prompts twice, so this can ask without first checking: on Android the contract
 * returns the existing answer, on iOS so does `requestAuthorization`. What it must not do is ask
 * again every time a heart is tapped, which is what [hasAsked] is for.
 */
@Composable
fun ReminderEffects() {
    val planRepository = koinInject<PlanRepository>()
    val contentRepository = koinInject<ContentRepository>()
    val scheduler = koinInject<ReminderScheduler>()
    val permissionRequester = rememberNotificationPermissionRequester()
    val scope = rememberCoroutineScope()

    val saved by planRepository.observeSaved().collectAsStateWithLifecycle(emptyList())
    val status by contentRepository.observeStatus().collectAsStateWithLifecycle()

    val hasSavedSlot = remember(saved) { saved.any { it.kind == SavedKind.SLOT } }

    // Deliberately not rememberSaveable. Surviving process death would mean a visitor who dismissed
    // the prompt months ago is never asked again on a device where the system would now allow it;
    // once per composition of the shell is the smaller mistake in both directions.
    var hasAsked by remember { mutableStateOf(false) }

    LaunchedEffect(hasSavedSlot) {
        if (!hasSavedSlot || hasAsked) return@LaunchedEffect

        hasAsked = true
        permissionRequester.request { scope.launch { scheduler.sync() } }
    }

    // The plan changing and the content changing are the two things that can alter the answer while
    // the app is open — a heart tapped, a refresh that moves a set. Both land here.
    LaunchedEffect(saved, status) {
        scheduler.sync()
    }

    // And on every resume, for everything that changed while the app was not running: notifications
    // switched off in settings, a reminder that has since fired, the day rolling over.
    LifecycleResumeEffect(Unit) {
        scope.launch { scheduler.sync() }
        onPauseOrDispose { }
    }
}
