package io.nicolaszurbuchen.yadlo.app.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Which tab is showing.
 *
 * Held outside the composition rather than as `remember` state because tab selection is not only
 * a tap: a typed annonce action (`programme(day?)`, `plus(entry)`) has to be able to send the
 * user to a tab from anywhere, and a notification tap will too.
 *
 * The start tab is [Tab.HOME] for now. DECISIONS.md § Settled has it following the Phase —
 * Accueil for 361 days, Programme for the four days of the festival — which needs the edition's
 * days, so it is wired in when the content slice lands. [select] is the seam it will use.
 */
class TabNavigator {
    private val _selectedTab = MutableStateFlow(Tab.HOME)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    fun select(tab: Tab) {
        _selectedTab.value = tab
    }
}
