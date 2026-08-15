package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.MonYadloNavigator
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation.WishlistDestination
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class MonYadloNavigatorImpl(
    private val appNavigator: AppNavigator,
) : MonYadloNavigator {
    override fun navigateToHappening(happeningId: String) {
        appNavigator.navigateTo(HappeningDestination(happeningId))
    }

    override fun navigateToWishlist() {
        appNavigator.navigateTo(WishlistDestination)
    }

    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
