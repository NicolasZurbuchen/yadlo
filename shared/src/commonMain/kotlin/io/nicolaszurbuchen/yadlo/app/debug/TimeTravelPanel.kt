package io.nicolaszurbuchen.yadlo.app.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.infra.format.formatAsShortDate
import io.nicolaszurbuchen.yadlo.infra.format.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.notification.NotificationTarget
import io.nicolaszurbuchen.yadlo.infra.notification.Notifier
import io.nicolaszurbuchen.yadlo.infra.notification.ScheduledNotification
import io.nicolaszurbuchen.yadlo.infra.notification.rememberNotificationPermissionRequester
import io.nicolaszurbuchen.yadlo.infra.platform.BuildFlags
import io.nicolaszurbuchen.yadlo.infra.time.TimeTravelClock
import io.nicolaszurbuchen.yadlo.infra.time.WallClock
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Moves the app's clock, so every state that depends on an instant can be reached from a device
 * instead of waited for.
 *
 * The whole app is a function of "now" — the Phase and its Accueil block stack, which FestivalDay
 * the Programme opens on, every live pill, every progress bar, the annonces window during LIVE.
 * None of it is reachable in August, and none of it is worth shipping unverified because the day it
 * is wrong is the one weekend nobody can fix it.
 *
 * Draws nothing unless the binary is a debug one, and the clock itself refuses to move in release
 * regardless, so there are two independent reasons this cannot reach a visitor.
 *
 * The strings are hardcoded English rather than resources: they are for whoever is holding the
 * device with the IDE open, and putting them in `strings.xml` would ship them.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeTravelPanel(modifier: Modifier = Modifier) {
    val flags = koinInject<BuildFlags>()
    if (!flags.isDebug) return

    val clock = koinInject<TimeTravelClock>()
    val repository = koinInject<ContentRepository>()
    val notifier = koinInject<Notifier>()
    val wallClock = koinInject<WallClock>()
    val permissionRequester = rememberNotificationPermissionRequester()
    val scope = rememberCoroutineScope()

    val simulated by clock.simulated.collectAsStateWithLifecycle()
    val status by repository.observeStatus().collectAsStateWithLifecycle()

    var isExpanded by remember { mutableStateOf(false) }

    val reading = simulated ?: clock.now()
    val readingLocal = reading.toLocalDateTime(FESTIVAL_TIME_ZONE)
    val label = "${reading.formatAsShortDate(FESTIVAL_TIME_ZONE)} ${reading.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)}"

    var dateField by remember(isExpanded) { mutableStateOf(readingLocal.date.toString()) }
    var timeField by
        remember(isExpanded) {
            mutableStateOf("${readingLocal.hour.toString().padStart(2, '0')}:${readingLocal.minute.toString().padStart(2, '0')}")
        }

    val days = (status as? ContentStatus.Ready)?.bundle?.edition?.days.orEmpty().sortedBy { it.start }
    val opensAt = days.firstOrNull()?.start
    val closesAt = days.lastOrNull()?.end

    Box(modifier = modifier.fillMaxSize()) {
        if (!isExpanded) {
            Text(
                text = if (simulated == null) "⏱ live" else "⏱ $label",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.appColors.onAccent,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = MaterialTheme.spacing.sm, bottom = COLLAPSED_BOTTOM_INSET)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.appColors.accent)
                        .clickable { isExpanded = true }
                        .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
            )
            return@Box
        }

        // A scrim that closes on tap, so the panel never traps whoever opened it.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.appColors.scrim)
                    .clickable { isExpanded = false },
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.sm)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.surface)
                    .padding(MaterialTheme.spacing.md),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (simulated == null) "Clock — live" else "Clock — simulated",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.appColors.textPrimary,
                )

                Text(
                    text = "Close",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.appColors.primary,
                    modifier = Modifier.clickable { isExpanded = false },
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.appColors.primary,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                OutlinedTextField(
                    value = dateField,
                    onValueChange = { dateField = it },
                    label = { Text("yyyy-mm-dd") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.weight(DATE_FIELD_WEIGHT),
                )

                OutlinedTextField(
                    value = timeField,
                    onValueChange = { timeField = it },
                    label = { Text("hh:mm") },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.weight(TIME_FIELD_WEIGHT),
                )
            }

            // Parsed on tap rather than on every keystroke: half a date is not a date, and an
            // error toast per character is worse than a button that does nothing until it can.
            val typedInstant =
                runCatching {
                    LocalDate.parse(dateField).atTime(LocalTime.parse(timeField)).toInstant(FESTIVAL_TIME_ZONE)
                }.getOrNull()

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                DebugAction(label = "Go", isEnabled = typedInstant != null) {
                    typedInstant?.let(clock::simulateAt)
                }

                DebugAction(label = "Live", isEnabled = simulated != null) {
                    clock.resume()
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                NUDGES.forEach { (nudgeLabel, step) ->
                    DebugAction(label = nudgeLabel) { clock.simulateAt(reading + step) }
                }
            }

            // **The one control on this panel the clock above cannot reach.** Reminders are scheduled
            // against WallClock, never the simulated one, because the OS compares them to wall time —
            // so jumping to the Saturday evening moves every screen and no alarm. This is the way to
            // see the notification pipe end to end without waiting: schedule, background the app, and
            // watch it arrive cold, which is the case that actually breaks.
            //
            // It replaces the real reminders for a minute, the same as any other pass. The next sync
            // puts them back, and one happens on the next resume.
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                DebugAction(label = "Reminder in 60 s") {
                    permissionRequester.request { granted ->
                        if (!granted) return@request

                        scope.launch {
                            notifier.replaceScheduled(
                                listOf(
                                    ScheduledNotification(
                                        id = DEBUG_REMINDER_ID,
                                        at = wallClock.now() + DEBUG_REMINDER_DELAY,
                                        title = "Test reminder",
                                        body = "Scheduled 60 s ago from the debug panel.",
                                        target = NotificationTarget.Programme,
                                        staleAfter = null,
                                    ),
                                ),
                            )
                        }
                    }
                }
            }

            // Straight at each Phase boundary, which is what "check every state" actually means:
            // the block stack on Accueil changes at each of these and nowhere in between.
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                if (closesAt != null) {
                    // Forwards, not backwards. ANNOUNCED keys off `slots.any()` rather than a
                    // countdown threshold, so with a published programme on hand there is no
                    // pre-festival OFF_SEASON to jump to at all — the only one that exists is the
                    // far side of the six weeks ENDED lasts. A preset that said OFF_SEASON and
                    // landed in ANNOUNCED was reporting a phase the app had not entered.
                    DebugAction(label = "OFF_SEASON") { clock.simulateAt(closesAt + OFF_SEASON_LEAD) }
                }

                if (opensAt != null) {
                    DebugAction(label = "ANNOUNCED") { clock.simulateAt(opensAt - ANNOUNCED_LEAD) }
                    DebugAction(label = "APPROACHING") { clock.simulateAt(opensAt - APPROACHING_LEAD) }
                }

                days.forEachIndexed { index, day ->
                    // Mid-afternoon on each day: the hour with the most overlapping Slots, so the
                    // Programme shows running, ending and starting-soon rows at once.
                    DebugAction(label = "LIVE ${day.name}") {
                        val afternoon =
                            day.start
                                .toLocalDateTime(FESTIVAL_TIME_ZONE)
                                .date
                                .atTime(MID_AFTERNOON)
                                .toInstant(FESTIVAL_TIME_ZONE)
                        clock.simulateAt(if (index == 0) maxOf(afternoon, day.start) else afternoon)
                    }
                }

                if (closesAt != null) {
                    DebugAction(label = "ENDED") { clock.simulateAt(closesAt + ENDED_LEAD) }
                }
            }
        }
    }
}

@Composable
private fun DebugAction(
    label: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (isEnabled) MaterialTheme.appColors.onPrimary else MaterialTheme.appColors.textTertiary,
        modifier =
            Modifier
                .clip(MaterialTheme.shapes.small)
                .background(
                    if (isEnabled) MaterialTheme.appColors.primary else MaterialTheme.appColors.surfaceRaised,
                )
                .clickable(enabled = isEnabled, onClick = onClick)
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
    )
}

private val NUDGES: List<Pair<String, Duration>> =
    listOf(
        "−1 d" to -(1.days),
        "−1 h" to -(1.hours),
        "−15 m" to -(15.minutes),
        "+15 m" to 15.minutes,
        "+1 h" to 1.hours,
        "+1 d" to 1.days,
    )

/** Clears the bottom navigation bar so the collapsed pill never sits on top of a tab. */
private val COLLAPSED_BOTTOM_INSET = 96.dp

private const val DATE_FIELD_WEIGHT = 1.4f
private const val TIME_FIELD_WEIGHT = 1f

/**
 * Past the six weeks ENDED lasts — measured from the last day's close, and with room for the
 * handover landing at 11:00 the morning after rather than at the gates shutting.
 */
private val OFF_SEASON_LEAD = 45.days

/** Comfortably outside the seven days APPROACHING claims, and inside the published programme. */
private val ANNOUNCED_LEAD = 30.days
private val APPROACHING_LEAD = 3.days
private val ENDED_LEAD = 1.days

private val MID_AFTERNOON = LocalTime(hour = 15, minute = 45)

/** Long enough to background the app before it fires, which is the state worth testing. */
private val DEBUG_REMINDER_DELAY = 60.seconds
private const val DEBUG_REMINDER_ID = "debug:reminder"
