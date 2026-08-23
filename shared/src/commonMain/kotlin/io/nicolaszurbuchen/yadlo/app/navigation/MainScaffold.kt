package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import io.nicolaszurbuchen.yadlo.app.design.component.YadloTopAppBar
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.notification.ReminderEffects
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.usecase.DerivePhaseUseCase
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation.SearchDestination
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.NavGraph
import io.nicolaszurbuchen.yadlo.infra.navigation.rememberNavEntries
import io.nicolaszurbuchen.yadlo.infra.notification.NotificationTarget
import io.nicolaszurbuchen.yadlo.infra.notification.NotificationTargetRelay
import io.nicolaszurbuchen.yadlo.infra.platform.BackHandler
import io.nicolaszurbuchen.yadlo.infra.time.AppClock
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.search_action

/**
 * The tab shell: four independent back stacks, one of which is visible.
 *
 * Each tab keeps its own stack rather than sharing one, because a fiche is reached from more than
 * one place — the same Happening opens from Programme and from Plus › Nourriture — and it has to
 * return to wherever it was opened from. A single shared stack would also stack tab roots on top
 * of each other, so backing out of Plus would land on a fiche the user left in Programme.
 */
@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    val appNavigator = koinInject<AppNavigator>()
    val tabNavigator = koinInject<TabNavigator>()
    val contentRepository = koinInject<ContentRepository>()
    val derivePhase = koinInject<DerivePhaseUseCase>()
    val clock = koinInject<AppClock>()
    val notificationRelay = koinInject<NotificationTargetRelay>()

    // Read here rather than by each tab's own store: the bar belongs to the shell, and four
    // screens deriving the same two strings is four places for them to drift apart.
    val status by contentRepository.observeStatus().collectAsStateWithLifecycle()
    val ready = status as? ContentStatus.Ready

    // **The dates come off the bar between editions.** They are the answer to "which weekend is
    // this?", and off season there is no weekend to be on the way to — the countdown on Accueil is
    // where a date eight months out belongs, next to the number of days that gives it a meaning.
    //
    // Recomputed when the content changes and when the debug clock is moved, but not on a ticker.
    // Every boundary that can hide or restore the dates is content-driven — a programme published,
    // an edition swapped — except the one at six weeks past the festival, which no session is going
    // to be open across. [AppClock.jumps] never emits in a release build, so this costs a
    // subscription that never fires and keeps the time-travel panel honest.
    var clockMoved by remember { mutableIntStateOf(0) }
    LaunchedEffect(clock) {
        clock.jumps.collect { clockMoved++ }
    }

    val phase =
        remember(ready, clockMoved) {
            derivePhase(
                days = ready?.bundle?.edition?.days.orEmpty(),
                hasPublishedProgramme = ready?.bundle?.edition?.slots.orEmpty().isNotEmpty(),
            )
        }

    // **The tab the app opens on, and the only place the Phase decides navigation.** Accueil for
    // 361 days of the year; Programme for the four the festival is running, because during LIVE
    // the question is "what is on now" and Accueil's honest answer to it is the other tab.
    //
    // A `remember` rather than a `LaunchedEffect`, and above the read of the selected tab rather
    // than below it: an effect runs after composition, so the shell would draw one frame of
    // Accueil on the Saturday morning before replacing it. Written here, [TabNavigator.selectStart]
    // has already moved before the flow below is first read. The same shape App.kt uses to install
    // the image loader.
    //
    // The shell is not composed until the content is Ready — App.kt holds the splash until then —
    // so the Phase is known on the first pass and there is no second chance to wait for.
    val isColdStart =
        remember(Unit) {
            tabNavigator.selectStart(if (phase == Phase.LIVE) Tab.PROGRAMME else Tab.HOME)
        }

    val selectedTab by tabNavigator.selectedTab.collectAsStateWithLifecycle()

    // Scheduling and the permission ask, both of which need the shell to exist and neither of which
    // draws anything. Kept in one composable rather than four effects inlined here.
    ReminderEffects()

    // Declared one by one rather than built in a loop: these are composables, and the call order
    // has to be identical on every recomposition. Each rememberNavEntries call is also its own
    // composition slot, which is what gives each tab decorator state of its own.
    val homeStack = rememberNavBackStack(navConfig, Tab.HOME.root)
    val programmeStack = rememberNavBackStack(navConfig, Tab.PROGRAMME.root)
    val monYadloStack = rememberNavBackStack(navConfig, Tab.MON_YADLO.root)
    val plusStack = rememberNavBackStack(navConfig, Tab.PLUS.root)

    val homeEntries = rememberNavEntries(homeStack)
    val programmeEntries = rememberNavEntries(programmeStack)
    val monYadloEntries = rememberNavEntries(monYadloStack)
    val plusEntries = rememberNavEntries(plusStack)

    val stacks =
        remember(homeStack, programmeStack, monYadloStack, plusStack) {
            mapOf(
                Tab.HOME to homeStack,
                Tab.PROGRAMME to programmeStack,
                Tab.MON_YADLO to monYadloStack,
                Tab.PLUS to plusStack,
            )
        }

    // **A notification tap arrives here, and it is the one thing allowed to move the visitor.**
    // It is written below the stacks rather than beside the other effects because it needs them: a
    // Slot reminder opens a fiche, and a fiche is a push onto the Programme tab's own stack, not a
    // tab switch. Pushing it there rather than onto whichever tab happens to be showing is what
    // makes backing out of it land on the Programme — the tab the reminder was about.
    //
    // Consumed rather than left set, because a target is an event: without that, every
    // recomposition and every rotation would send the visitor back to the same fiche.
    val notificationTarget by notificationRelay.target.collectAsStateWithLifecycle()
    LaunchedEffect(notificationTarget) {
        when (val target = notificationTarget) {
            null -> {
                return@LaunchedEffect
            }

            NotificationTarget.Home -> {
                tabNavigator.select(Tab.HOME)
            }

            NotificationTarget.Programme -> {
                tabNavigator.select(Tab.PROGRAMME)
            }

            is NotificationTarget.Happening -> {
                tabNavigator.select(Tab.PROGRAMME)
                programmeStack.add(HappeningDestination(target.id))
            }
        }

        notificationRelay.consume()
    }

    // **A cold start opens each tab at its root, and the saved stacks are for rotation only.**
    // Navigation 3 restores every stack from saved state, which does not distinguish a rotation from
    // a process Android killed while the app was in the background — and the two want opposite
    // things. Restoring after a rotation is the whole point; restoring after a kill dropped the
    // visitor several screens deep into a tab they had not chosen, since the selected tab is not
    // saved and had already gone back to the Phase's answer. Half-restored was the worst of both, so
    // this makes a cold start clean: the Phase's tab, every stack at its root.
    //
    // In a `remember` rather than an effect, and above the reads below, because an effect runs after
    // composition — NavDisplay would draw one frame of the screen being popped. The same reason
    // selectStart is written where it is.
    remember(Unit) {
        if (isColdStart) stacks.values.forEach { it.popToRoot() }
    }

    val currentStack = stacks.getValue(selectedTab)
    val currentEntries =
        when (selectedTab) {
            Tab.HOME -> homeEntries
            Tab.PROGRAMME -> programmeEntries
            Tab.MON_YADLO -> monYadloEntries
            Tab.PLUS -> plusEntries
        }
    val isAtTabRoot = currentStack.size <= 1

    // **The magnifier belongs to the shell, and that is what lets it mean "everything".** This bar
    // is the same on all four tabs — the festival's name and the edition's dates, never a tab title
    // — so an action in it inherits that rather than reading as a control over the tab underneath.
    // The corpus behind it is one index, and the results say so by answering with headings from
    // places the reader did not come from.
    //
    // **Not on Accueil**, which carries the search block itself: an icon and a field on one screen
    // are two doors to the same room, side by side. The block is the one that teaches the app has a
    // search, so it wins where they collide, and the icon covers the three tabs that have no room
    // for a field.
    //
    // **No Phase gate, which is a reversal** — DECISIONS.md § Search is enabled all year. Half the
    // corpus never expires: paiement, horaires, comment venir, devenir bénévole and nous écrire are
    // live truth rather than an edition, and off season they are the most useful thing in the app.
    // The other half is last July, which is the edition the bundle holds anyway.
    val showsSearch = selectedTab != Tab.HOME

    // SideEffect, not LaunchedEffect: this has to be true before the frame the user can touch.
    // LaunchedEffect publishes on a coroutine after composition, which leaves a window where the
    // bar has already switched tabs but the navigator still points at the tab being left, so a
    // tap landing in that window pushes onto the wrong stack.
    SideEffect {
        appNavigator.attach(currentStack)
    }

    // Only the root-level case is handled here. Deeper than that, NavDisplay is the inner back
    // handler and pops its own stack. On iOS there is no system back and this is a no-op.
    BackHandler(enabled = isAtTabRoot && selectedTab != Tab.HOME) {
        tabNavigator.select(Tab.HOME)
    }

    // Measured from the bars themselves rather than assumed from a Material token, and held across
    // the frames they are hidden for. See [TabChromeInsets] for why it must not move.
    val density = LocalDensity.current
    var chrome by remember { mutableStateOf(TabChromeInsets()) }

    // The ground the tabs are drawn on. A Scaffold painted this for free and a Box does not, so
    // dropping the Scaffold left every tab falling through to the platform root's own white.
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
        // The graph owns the whole window at every depth. Nothing about its size depends on whether
        // the current tab is at its root, which is what stops the screen behind a push from being
        // re-measured while it is still on screen.
        //
        // LocalContentColor is the other thing the Scaffold used to hand down, through the Surface
        // it wraps its content in. Material's ripple defaults to it, so with nothing providing it
        // the four tabs fell back to foundation's plain black — a tap on an annonce lit up in a
        // colour belonging to no theme, and in dark mode barely lit up at all.
        CompositionLocalProvider(
            LocalTabChromeInsets provides chrome,
            LocalContentColor provides MaterialTheme.appColors.textPrimary,
        ) {
            NavGraph(
                entries = currentEntries,
                onBack = { currentStack.popOne() },
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Both bars belong to the tab roots — a fiche is full-screen with a back chevron instead,
        // and the prototypes show no bar on a detail screen. They slide out rather than vanish, so
        // the title still covers the status bar for as long as the screen under it is still there.
        AnimatedVisibility(
            visible = isAtTabRoot,
            enter = slideInVertically { -it },
            exit = slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            // Yadlo, and when. On every tab root, so the answer to "which weekend is this?" is
            // never more than a glance away and no screen has to spend a line of its own saying it.
            YadloTopAppBar(
                title = ready?.bundle?.festival?.name.orEmpty(),
                subtitle = ready?.bundle?.edition?.days?.takeUnless { phase == Phase.OFF_SEASON }?.let(::formatEditionDates),
                actions = {
                    if (showsSearch) {
                        // Pushed onto the tab that is showing, like every other detail screen, so
                        // backing out of a search lands on the tab it was opened from.
                        IconButton(onClick = { appNavigator.navigateTo(SearchDestination) }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(Res.string.search_action),
                            )
                        }
                    }
                },
                modifier =
                    Modifier.onSizeChanged { size ->
                        chrome = chrome.copy(top = with(density) { size.height.toDp() })
                    },
            )
        }

        AnimatedVisibility(
            visible = isAtTabRoot,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            MainNavigationBar(
                selectedTab = selectedTab,
                onTabClick = { tab ->
                    if (tab == selectedTab) {
                        // Re-tapping the active tab returns to its root. The standard way out of a
                        // deep stack without hunting for the back gesture.
                        stacks.getValue(tab).popToRoot()
                    } else {
                        tabNavigator.select(tab)
                    }
                },
                modifier =
                    Modifier.onSizeChanged { size ->
                        chrome = chrome.copy(bottom = with(density) { size.height.toDp() })
                    },
            )
        }
    }
}

// A tab's root is not poppable. NavDisplay throws the moment it is handed an empty list, and it
// renders in the same frame the list is mutated, so an unguarded pop turns a double-tap into a
// crash rather than a no-op.
private fun NavBackStack<NavKey>.popOne() {
    if (size > 1) removeAt(size - 1)
}

private fun NavBackStack<NavKey>.popToRoot() {
    while (size > 1) removeAt(size - 1)
}

/**
 * `10 – 12.07.2026`, collapsing whatever the two ends share. Null when the edition publishes no
 * days, which is the between-editions case rather than an error.
 */
internal fun formatEditionDates(days: List<FestivalDay>): String? {
    val first = days.minByOrNull { it.start }?.start?.toLocalDateTime(FESTIVAL_TIME_ZONE)?.date ?: return null
    val last = days.maxByOrNull { it.start }?.start?.toLocalDateTime(FESTIVAL_TIME_ZONE)?.date ?: return null

    val firstDay = first.day.toString().padStart(2, '0')
    val lastDay = last.day.toString().padStart(2, '0')
    val firstMonth = first.month.number.toString().padStart(2, '0')
    val lastMonth = last.month.number.toString().padStart(2, '0')

    return when {
        first == last -> "$lastDay.$lastMonth.${last.year}"
        first.year != last.year -> "$firstDay.$firstMonth.${first.year} – $lastDay.$lastMonth.${last.year}"
        first.month != last.month -> "$firstDay.$firstMonth – $lastDay.$lastMonth.${last.year}"
        else -> "$firstDay – $lastDay.$lastMonth.${last.year}"
    }
}

/**
 * **The same blue as the bar at the top, which is what makes the two read as one frame.** It was
 * Material's own `surfaceContainer` — a near-white in light and a near-black in dark — so the app
 * had a coloured band above the page and a neutral one below it, and the ground the tabs are drawn
 * on ran out at a different place from the chrome that holds them.
 *
 * **The accent stays, and it had to change step to survive the move.** The selected tab keeps its
 * rose pill; what it cannot keep is `accentSubtle`, which is chosen against a page ground and
 * measures 1.34:1 on the dark bandeau blue — an indicator that is simply not there. See
 * [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.accentChrome], which is the same accent at
 * the step each theme's bar can actually carry.
 *
 * Everything not in the pill takes the bar's own ink, selected and unselected alike, exactly as the
 * top bar does with its title and its actions. The pill is the selection cue; a second one in the
 * label would only be legible to someone comparing two labels, which is not how a tab bar is read.
 */
@Composable
private fun MainNavigationBar(
    selectedTab: Tab,
    onTabClick: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        containerColor = MaterialTheme.appColors.primarySubtle,
        contentColor = MaterialTheme.appColors.onPrimarySubtle,
        modifier = modifier,
    ) {
        Tab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabClick(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(text = stringResource(tab.label)) },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.appColors.onAccentChrome,
                        selectedTextColor = MaterialTheme.appColors.onPrimarySubtle,
                        indicatorColor = MaterialTheme.appColors.accentChrome,
                        unselectedIconColor = MaterialTheme.appColors.onPrimarySubtle,
                        unselectedTextColor = MaterialTheme.appColors.onPrimarySubtle,
                    ),
            )
        }
    }
}
