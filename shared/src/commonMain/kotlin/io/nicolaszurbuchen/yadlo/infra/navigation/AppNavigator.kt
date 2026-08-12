package io.nicolaszurbuchen.yadlo.infra.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class AppNavigator {
    private var backStack: NavBackStack<NavKey>? = null

    fun attach(backStack: NavBackStack<NavKey>) {
        this.backStack = backStack
    }

    fun navigateTo(key: NavKey) {
        backStack?.add(key)
    }

    /**
     * Pops the top entry, unless it is the only one left.
     *
     * A back stack's root is not poppable: NavDisplay throws `NavDisplay backstack cannot be
     * empty` the moment it is handed nothing to render, and it renders during the same frame the
     * list is mutated. Two taps on a back button land before the screen is torn down, so without
     * this guard an ordinary double-tap crashes the app.
     */
    fun navigateBack() {
        val stack = backStack ?: return
        if (stack.size > 1) {
            stack.removeAt(stack.size - 1)
        }
    }

    fun popUpTo(predicate: (NavKey) -> Boolean) {
        val stack = backStack ?: return
        val index = stack.indexOfFirst(predicate)
        if (index != -1) {
            while (stack.size > index + 1) {
                stack.removeAt(stack.size - 1)
            }
        }
    }
}
