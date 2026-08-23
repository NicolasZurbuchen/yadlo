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
 * **Permission is requested when a Slot is hearted, never on launch.** Asked at startup, the prompt
 * is an app the visitor has not used yet asking to interrupt them, and most people say no — which
 * also loses the reminders they would have wanted, because both platforms treat a refusal as final.
 * Asked here it is a prompt about the thing they just did. There is one attempt to spend and this is
 * where it buys the most.
 *
 * **Watching whether the Plan *has* Slots is not the same question as whether one was just added**,
 * and reading the first is how the prompt ended up at launch: anybody who had already saved
 * something arrived with the condition true and got asked before they had touched anything. Nor is
 * it enough to skip the first emission — the plan loads from the database a beat after this starts
 * listening, so the very first thing it reports is a rise from nothing that no one performed.
 *
 * So [knownSlotCount] holds what the plan turned out to already contain, and only a count rising
 * *above* that counts as an act. The cost is that somebody who saved Slots before this feature
 * existed is not asked until they save one more, which is the right way round: they are owed a
 * prompt about something they are doing, not about something the app found.
 *
 * Neither platform prompts twice, so this can ask without first checking whether it already has an
 * answer: on Android the contract returns the existing one, on iOS so does `requestAuthorization`.
 */
@Composable
fun ReminderEffects() {
    val planRepository = koinInject<PlanRepository>()
    val contentRepository = koinInject<ContentRepository>()
    val scheduler = koinInject<ReminderScheduler>()
    val permissionRequester = rememberNotificationPermissionRequester()
    val scope = rememberCoroutineScope()

    // Null until the database answers, which is the distinction the ask below is built on. An empty
    // list would say "nothing is saved" a beat before the plan says otherwise.
    val saved by planRepository.observeSaved().collectAsStateWithLifecycle(null)
    val status by contentRepository.observeStatus().collectAsStateWithLifecycle()

    val savedSlotCount = saved?.count { it.kind == SavedKind.SLOT }

    var knownSlotCount by remember { mutableStateOf<Int?>(null) }

    // Deliberately not rememberSaveable. Surviving process death would mean a visitor who dismissed
    // the prompt months ago is never asked again on a device where the system would now allow it;
    // once per composition of the shell is the smaller mistake in both directions.
    var hasAsked by remember { mutableStateOf(false) }

    LaunchedEffect(savedSlotCount) {
        val count = savedSlotCount ?: return@LaunchedEffect
        val known = knownSlotCount
        knownSlotCount = count

        // known == null is the plan as it already was, not something the visitor just did.
        if (known == null || count <= known || hasAsked) return@LaunchedEffect

        hasAsked = true
        permissionRequester.request { scope.launch { scheduler.sync() } }
    }

    // The plan changing and the content changing are the two things that can alter the answer while
    // the app is open — a heart tapped, a refresh that moves a set. Both land here.
    LaunchedEffect(saved, status) {
        if (saved == null) return@LaunchedEffect

        scheduler.sync()
    }

    // And on every resume, for everything that changed while the app was not running: notifications
    // switched off in settings, a reminder that has since fired, the day rolling over.
    LifecycleResumeEffect(Unit) {
        scope.launch { scheduler.sync() }
        onPauseOrDispose { }
    }
}
