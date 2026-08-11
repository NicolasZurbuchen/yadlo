package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class HappeningNavigatorImpl(
    private val appNavigator: AppNavigator,
) : HappeningNavigator {
    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
