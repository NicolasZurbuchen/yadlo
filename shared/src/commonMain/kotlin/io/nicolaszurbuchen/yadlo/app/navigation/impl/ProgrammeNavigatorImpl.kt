package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation.ProgrammeNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class ProgrammeNavigatorImpl(
    private val appNavigator: AppNavigator,
) : ProgrammeNavigator {
    override fun navigateToHappening(happeningId: String) {
        appNavigator.navigateTo(HappeningDestination(happeningId))
    }
}
