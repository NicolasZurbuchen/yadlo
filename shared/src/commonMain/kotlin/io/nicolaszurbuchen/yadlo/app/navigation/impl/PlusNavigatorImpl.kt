package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessibilityDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AssistanceDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.FaqDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.HoursDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PaymentDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PlusNavigator
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsDestination
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class PlusNavigatorImpl(
    private val appNavigator: AppNavigator,
) : PlusNavigator {
    override fun navigateToStands() {
        appNavigator.navigateTo(StandsDestination)
    }

    override fun navigateToPayment() {
        appNavigator.navigateTo(PaymentDestination)
    }

    override fun navigateToAccess() {
        appNavigator.navigateTo(AccessDestination)
    }

    override fun navigateToAccessibility() {
        appNavigator.navigateTo(AccessibilityDestination)
    }

    override fun navigateToHours() {
        appNavigator.navigateTo(HoursDestination)
    }

    override fun navigateToAssistance() {
        appNavigator.navigateTo(AssistanceDestination)
    }

    override fun navigateToFaq() {
        appNavigator.navigateTo(FaqDestination)
    }

    /**
     * The fiche the Programme and Mon Yadlo also open. Plus is the browse half of the Wishlist's
     * recall half, and both halves end at the same screen — which is where the single heart that
     * can add or remove a Stand lives.
     */
    override fun navigateToHappening(happeningId: String) {
        appNavigator.navigateTo(HappeningDestination(happeningId))
    }

    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
