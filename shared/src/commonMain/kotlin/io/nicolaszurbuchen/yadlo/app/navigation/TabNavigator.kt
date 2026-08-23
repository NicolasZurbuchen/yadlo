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
 * [Tab.HOME] is the field's default rather than the app's answer. The real start tab follows the
 * Phase — DECISIONS.md § The default tab follows the phase: Accueil for 361 days, Programme for
 * the four days of the festival — and the Phase needs the edition's days, which arrive after this
 * object is built. [selectStart] is where that lands.
 */
class TabNavigator {
    private val _selectedTab = MutableStateFlow(Tab.HOME)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    private var hasStarted = false

    /**
     * The tab the app opens on, decided once from the Phase and then never again.
     *
     * **A start destination, not a redirect, and the difference is the whole reason this is not
     * [select].** LIVE begins at midnight on the Friday, and someone reading the annonces at
     * 23:59 must not have the screen pulled out from under them as the date turns. The same goes
     * for every later call: the shell recomposes for reasons of its own — a rotation, a content
     * refresh — and each one would otherwise undo whichever tab the visitor had chosen since.
     *
     * Idempotent rather than guarded at the call site, because the caller is a composition and
     * cannot promise to run exactly once.
     *
     * **Returns whether this call was the first**, which is the shell's only way to tell a cold
     * start from a rotation. This object is a singleton for the life of the process, so a false
     * here means the composition came back — a configuration change, the content going away and
     * returning — while a true means the process itself is new. The tab back stacks are restored
     * from saved state either way, and only one of those two cases wants them restored.
     */
    fun selectStart(tab: Tab): Boolean {
        if (hasStarted) return false

        hasStarted = true
        _selectedTab.value = tab

        return true
    }

    fun select(tab: Tab) {
        // A deliberate choice is also the end of the start-up question: without this, content
        // arriving a beat after the first tap would still get to overrule it.
        hasStarted = true
        _selectedTab.value = tab
    }
}
