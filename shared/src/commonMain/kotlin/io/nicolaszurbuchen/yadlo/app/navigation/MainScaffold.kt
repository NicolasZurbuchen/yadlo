package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.NavGraph
import io.nicolaszurbuchen.yadlo.infra.navigation.rememberNavEntries
import io.nicolaszurbuchen.yadlo.infra.platform.BackHandler
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
    val selectedTab by tabNavigator.selectedTab.collectAsStateWithLifecycle()

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

    Scaffold(
        bottomBar = {
            // The bar belongs to the tab roots. A fiche is full-screen, with a back chevron
            // instead — the prototypes show no bar on a detail screen.
            if (isAtTabRoot) {
                MainNavigationBar(
                    selectedTab = selectedTab,
                    onTabClick = { tab ->
                        if (tab == selectedTab) {
                            // Re-tapping the active tab returns to its root. The standard way out
                            // of a deep stack without hunting for the back gesture.
                            stacks.getValue(tab).popToRoot()
                        } else {
                            tabNavigator.select(tab)
                        }
                    },
                )
            }
        },
        modifier = modifier,
    ) { contentPadding ->
        NavGraph(
            entries = currentEntries,
            onBack = { currentStack.popOne() },
            modifier = Modifier.padding(contentPadding),
        )
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
