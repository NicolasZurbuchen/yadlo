package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import io.nicolaszurbuchen.yadlo.app.notification.ReminderEffects
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.usecase.DerivePhaseUseCase
import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.design.component.YadloTopAppBar
import io.nicolaszurbuchen.yadlo.design.theme.LocalTabChromeInsets
import io.nicolaszurbuchen.yadlo.design.theme.TabChromeInsets
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation.SearchDestination
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.NAV_SLIDE_MILLIS
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
 * The tab shell: four independent back stacks, one of which is visible — DECISIONS.md § Each tab
 * keeps its own back stack.
 */
@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    val appNavigator = koinInject<AppNavigator>()
    val tabNavigator = koinInject<TabNavigator>()
    val contentRepository = koinInject<ContentRepository>()
    val derivePhase = koinInject<DerivePhaseUseCase>()
    val clock = koinInject<AppClock>()
    val notificationRelay = koinInject<NotificationTargetRelay>()

    val status by contentRepository.observeStatus().collectAsStateWithLifecycle()
    val ready = status as? ContentStatus.Ready

    // Recomputed on content and on a debug clock jump, never on a ticker — DECISIONS.md § The dates
    // come off the bar between editions. [AppClock.jumps] never emits in a release build.
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

    // A `remember` above the read below, never an effect: an effect runs after composition, so the
    // shell would draw one frame of Accueil on the Saturday morning before replacing it. See
    // DECISIONS.md § It is a start destination, not a redirect.
    val isColdStart =
        remember(Unit) {
            tabNavigator.selectStart(if (phase == Phase.LIVE) Tab.PROGRAMME else Tab.HOME)
        }

    val selectedTab by tabNavigator.selectedTab.collectAsStateWithLifecycle()

    ReminderEffects()

    // One call per stack and never a loop: these are composables, so the call order has to be
    // identical on every recomposition. See rememberNavEntries.
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

    // A reminder pushes onto the Programme's own stack rather than onto whichever tab is showing —
    // DECISIONS.md § Notifications. Consumed because a target is an event: left set, every
    // recomposition would send the visitor back to the same fiche.
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

    // DECISIONS.md § A cold start opens every tab at its root. In a `remember` above the reads
    // below, for the same reason selectStart is.
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

    // DECISIONS.md § One transition, spelled out once
    val slideTowards = rememberSlideDirection(selectedTab, currentStack.size)

    // DECISIONS.md § The bar can carry it because the bar is not a tab’s. In every Phase, and on
    // the three tabs with no room for the block — § Search.
    val showsSearch = selectedTab != Tab.HOME

    // SideEffect, not LaunchedEffect: a coroutine publishes after composition, leaving a window in
    // which a tap would push onto the tab being left rather than the one now showing.
    SideEffect {
        appNavigator.attach(currentStack)
    }

    // Only the root-level case: deeper than that, NavGraph's own handler pops the stack first. A
    // no-op on iOS, which has no system back.
    BackHandler(enabled = isAtTabRoot && selectedTab != Tab.HOME) {
        tabNavigator.select(Tab.HOME)
    }

    // Measured rather than assumed, and held across the frames the bars are hidden for — see
    // [TabChromeInsets] for why it must not move.
    val density = LocalDensity.current
    var chrome by remember { mutableStateOf(TabChromeInsets()) }

    // A Box paints no ground of its own, so without this every tab falls through to the platform
    // root's white.
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
        // The graph owns the whole window at every depth — see [TabChromeInsets]. Material's ripple
        // reads LocalContentColor, and nothing else provides it here, so without it a tap lights up
        // in foundation's plain black.
        CompositionLocalProvider(
            LocalTabChromeInsets provides chrome,
            LocalContentColor provides MaterialTheme.appColors.textPrimary,
        ) {
            NavGraph(
                entries = currentEntries,
                onBack = { currentStack.popOne() },
                slideTowards = slideTowards,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Both bars belong to the tab roots, and travel with them — DECISIONS.md § One transition,
        // spelled out once. Enter and exit are the two ends of one movement, hence the opposite signs.
        AnimatedVisibility(
            visible = isAtTabRoot,
            enter = slideInHorizontally(tween(NAV_SLIDE_MILLIS)) { if (slideTowards == SlideDirection.Left) it else -it },
            exit = slideOutHorizontally(tween(NAV_SLIDE_MILLIS)) { if (slideTowards == SlideDirection.Left) -it else it },
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            YadloTopAppBar(
                title = ready?.bundle?.festival?.name.orEmpty(),
                subtitle = ready?.bundle?.edition?.days?.takeUnless { phase == Phase.OFF_SEASON }?.let(::formatEditionDates),
                actions = {
                    if (showsSearch) {
                        // Pushed onto the tab showing, so backing out lands where it was opened from.
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
            enter = slideInHorizontally(tween(NAV_SLIDE_MILLIS)) { if (slideTowards == SlideDirection.Left) it else -it },
            exit = slideOutHorizontally(tween(NAV_SLIDE_MILLIS)) { if (slideTowards == SlideDirection.Left) -it else it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            MainNavigationBar(
                selectedTab = selectedTab,
                onTabClick = { tab ->
                    if (tab == selectedTab) {
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

// NavDisplay throws when handed an empty list and renders in the same frame the list is mutated, so
// an unguarded pop turns a double-tap into a crash rather than a no-op.
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

@Composable
private fun rememberSlideDirection(
    selectedTab: Tab,
    depth: Int,
): SlideDirection {
    var previousTab by remember { mutableStateOf(selectedTab) }
    var previousDepth by remember { mutableIntStateOf(depth) }
    var towards by remember { mutableStateOf(SlideDirection.Left) }

    // Derived in composition, not in an effect: the entries list changes in the same frame the tab
    // does and the display reads this on that frame, so an effect would publish it one frame late.
    if (selectedTab != previousTab || depth != previousDepth) {
        towards =
            when {
                selectedTab != previousTab -> {
                    if (selectedTab.ordinal > previousTab.ordinal) SlideDirection.Left else SlideDirection.Right
                }

                depth > previousDepth -> {
                    SlideDirection.Left
                }

                else -> {
                    SlideDirection.Right
                }
            }
        previousTab = selectedTab
        previousDepth = depth
    }

    return towards
}

@Composable
private fun MainNavigationBar(
    selectedTab: Tab,
    onTabClick: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        // A NavigationBar consumes this inset for free and a Surface does not — DECISIONS.md § It
        // clears the gesture bar itself. Inside onSizeChanged, so the lists underneath pad by the
        // whole of what the bar covers rather than by the pill alone.
        modifier =
            modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(BAR_MARGIN),
    ) {
        Surface(
            color = MaterialTheme.appColors.primarySubtle,
            contentColor = MaterialTheme.appColors.onPrimarySubtle,
            shape = CircleShape,
            shadowElevation = BAR_ELEVATION,
        ) {
            // A Row rather than ShortNavigationBar: both of that component's measure policies open
            // with `val width = constraints.maxWidth`, so it can never wrap its content.
            //
            // One padding on all four sides, which is what puts the same gap beside the end bubbles
            // as above and below them.
            EqualWidthRow(
                gap = ITEM_GAP,
                modifier = Modifier.selectableGroup().padding(BAR_PADDING),
            ) {
                Tab.entries.forEach { tab ->
                    TabItem(
                        tab = tab,
                        selected = tab == selectedTab,
                        onClick = { onTabClick(tab) },
                    )
                }
            }
        }
    }
}

/**
 * Lays out its children in a row, every one as wide as the widest of them.
 *
 * A `Row` with a weight on each child cannot do this. A weight is a share of the row's total, so
 * four equal weights give each child the *average* width — which squeezes the longest label, the
 * one case the whole thing exists for.
 */
@Composable
private fun EqualWidthRow(
    gap: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        // Intrinsics rather than a first measuring pass: a Measurable may only be measured once,
        // so the widest cannot be found by measuring everyone and then measuring them again.
        val gaps = gap.roundToPx() * (measurables.size - 1).coerceAtLeast(0)
        // Plus a couple of points, because an intrinsic width is a *measurement* of the text and
        // the glyphs draw a hair wider than it. Sized to the intrinsic exactly, the longest label
        // in the row loses the edge of its first and last letter.
        val needed = measurables.maxOf { it.maxIntrinsicWidth(constraints.maxHeight) } + INTRINSIC_SLACK.roundToPx()

        // **The breath is added here rather than to the item's own padding, and that is the whole
        // point of it.** Padding is inside the width a name asks for, so a cap that has to take
        // something back takes it out of the name. Added on top, it is the first thing surrendered
        // and the label is the last — which is what keeps every tab readable at a large system font
        // and on a narrow screen, where there is no room for it anyway.
        val itemWidth =
            if (constraints.hasBoundedWidth) {
                (needed + ITEM_BREATH.roundToPx()).coerceAtMost((constraints.maxWidth - gaps) / measurables.size)
            } else {
                needed + ITEM_BREATH.roundToPx()
            }

        val placeables = measurables.map { it.measure(Constraints.fixedWidth(itemWidth)) }
        val width = itemWidth * placeables.size + gaps
        val height = placeables.maxOf { it.height }

        layout(width, height) {
            var x = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x, (height - placeable.height) / 2)
                x += placeable.width + gap.roundToPx()
            }
        }
    }
}

/**
 * One tab: its icon over its name, and a bubble around both when it is the one showing.
 *
 * Hand-rolled rather than a `ShortNavigationBarItem`, whose two icon positions each give half of
 * this — `Top` puts the label below the bubble instead of inside it, and `Start` puts the bubble
 * around both but lays them out in a row.
 */
@Composable
private fun TabItem(
    tab: Tab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // accentChrome rather than accentSubtle, which is unreadable on this blue — DECISIONS.md § The
    // chrome is one frame.
    val bubble by
        animateColorAsState(
            targetValue = if (selected) MaterialTheme.appColors.accentChrome else Color.Transparent,
            label = "bubble",
        )
    val ink by
        animateColorAsState(
            targetValue =
                if (selected) MaterialTheme.appColors.onAccentChrome else MaterialTheme.appColors.onPrimarySubtle,
            label = "ink",
        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ICON_TO_LABEL),
        modifier =
            modifier
                // A stadium inside a stadium, so the two curves stay concentric and one padding is
                // the same distance all the way round the corner.
                .clip(CircleShape)
                .background(bubble)
                .selectable(selected = selected, role = Role.Tab, onClick = onClick)
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
    ) {
        Icon(
            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
            // The label below says it, and Role.Tab merges the two into one announcement.
            contentDescription = null,
            tint = ink,
        )

        Text(
            text = stringResource(tab.label),
            style = MaterialTheme.typography.labelMedium,
            color = ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// How far the pill lifts off the three edges it no longer touches. Applied after the navigation-bar
// inset, so it is clearance from the gesture bar rather than to it.
private val BAR_MARGIN = 12.dp

// The gap around the bubbles, on all four sides — see the row above for why it is one number.
private val BAR_PADDING = 8.dp

// Enough that two bubbles never meet while one is fading out under the other, and not so much that
// the four stop reading as one control.
private val ITEM_GAP = 4.dp

// What a measured text width under-reports by. Two points covers Barlow at this size; it is a
// fudge over Compose rather than a design value, which is why it is not on the spacing scale.
private val INTRINSIC_SLACK = 2.dp

// What every tab is given beyond the longest name, when the screen can afford it. See the layout
// for why it is not the item's padding.
private val ITEM_BREATH = 4.dp

// Tighter than any step on the spacing scale, because the icon and the name are one label rather
// than two stacked things.
private val ICON_TO_LABEL = 2.dp

// The only raised surface in the app, and on the dark theme most of what separates the pill from
// the page behind it — DECISIONS.md § The one shadow in the app.
private val BAR_ELEVATION = 6.dp
