package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.NavGraph
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

    // Declared one by one rather than built in a loop: rememberNavBackStack is a composable, and
    // the call order has to be identical on every recomposition.
    val homeStack = rememberNavBackStack(navConfig, Tab.HOME.root)
    val programmeStack = rememberNavBackStack(navConfig, Tab.PROGRAMME.root)
    val monYadloStack = rememberNavBackStack(navConfig, Tab.MON_YADLO.root)
    val plusStack = rememberNavBackStack(navConfig, Tab.PLUS.root)

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
    val isAtTabRoot = currentStack.size <= 1

    // The navigator always points at the stack the user is looking at, so a feature calling
    // navigateTo lands in the right tab without ever naming one.
    LaunchedEffect(currentStack) {
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
                            while (stacks.getValue(tab).size > 1) {
                                stacks.getValue(tab).removeLastOrNull()
                            }
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
            backStack = currentStack,
            onBack = { currentStack.removeLastOrNull() },
            modifier = Modifier.padding(contentPadding),
        )
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
