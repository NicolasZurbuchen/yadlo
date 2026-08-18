package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.NavGraph
import io.nicolaszurbuchen.yadlo.infra.navigation.rememberNavEntries
import io.nicolaszurbuchen.yadlo.infra.platform.BackHandler
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

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
    val selectedTab by tabNavigator.selectedTab.collectAsStateWithLifecycle()

    // Read here rather than by each tab's own store: the bar belongs to the shell, and four
    // screens deriving the same two strings is four places for them to drift apart.
    val status by contentRepository.observeStatus().collectAsStateWithLifecycle()
    val ready = status as? ContentStatus.Ready

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

    val currentStack = stacks.getValue(selectedTab)
    val currentEntries =
        when (selectedTab) {
            Tab.HOME -> homeEntries
            Tab.PROGRAMME -> programmeEntries
            Tab.MON_YADLO -> monYadloEntries
            Tab.PLUS -> plusEntries
        }
    val isAtTabRoot = currentStack.size <= 1

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
            MainTopAppBar(
                title = ready?.bundle?.festival?.name.orEmpty(),
                editionDates = ready?.bundle?.edition?.days?.let(::formatEditionDates).orEmpty(),
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
 * Yadlo, and when. On every tab root, so the answer to "which weekend is this?" is never more than
 * a glance away and no screen has to spend a line of its own saying it.
 *
 * **The bandeau blue, not the page ground.** Material's default container is `surface`, which is
 * white in light — so the bar and the screen under it were one undifferentiated field and the app
 * opened looking like nothing in particular. [io.nicolaszurbuchen.yadlo.app.design.theme.AppColors.primarySubtle]
 * is the blue from yadlo.ch and the app's quietest way of saying "this is the festival speaking";
 * it always carries dark ink, which AppColorTest holds rather than leaving as prose.
 *
 * The dates are numeric and Swiss-ordered rather than written out, for the same reason the annonce
 * dates are: a month name is the first thing that needs translating, and the language structure is
 * not decided yet. Revisit when it is.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(
    title: String,
    editionDates: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            // The dates sit beside the name on the same baseline rather than across the bar: they
            // are a subtitle to it, and pinned right they read as an unrelated status field.
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.appColors.onPrimarySubtle,
                    modifier = Modifier.alignByBaseline(),
                )

                Text(
                    text = editionDates,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.appColors.onPrimarySubtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alignByBaseline(),
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.appColors.primarySubtle,
                scrolledContainerColor = MaterialTheme.appColors.primarySubtle,
            ),
        modifier = modifier,
    )
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
private fun MainNavigationBar(
    selectedTab: Tab,
    onTabClick: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
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
            )
        }
    }
}
