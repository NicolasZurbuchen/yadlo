package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloRoute
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.WishlistRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class MonYadloNavKeyHandler(
    private val navigator: MonYadloNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<MonYadloMainDestination> {
            MonYadloRoute(
                onNavigateToHappening = { id -> navigator.navigateToHappening(id) },
                onNavigateToWishlist = { navigator.navigateToWishlist() },
            )
        }

        entry<WishlistDestination> {
            WishlistRoute(
                onNavigateBack = { navigator.navigateBack() },
                onNavigateToHappening = { id -> navigator.navigateToHappening(id) },
            )
        }
    }
}
