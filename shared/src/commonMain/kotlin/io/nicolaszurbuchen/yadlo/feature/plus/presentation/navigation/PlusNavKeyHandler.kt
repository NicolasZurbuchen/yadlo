package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility.AccessibilityRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusEntry
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class PlusNavKeyHandler(
    private val navigator: PlusNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        // The root hands back which row was tapped rather than a destination, because a Route may
        // only take lambdas and the screen has no business naming a NavKey. This `when` is the one
        // place the tab's rows and the app's back stack meet.
        entry<PlusDestination> {
            PlusRoute(
                onNavigateToEntry = { entry ->
                    when (entry) {
                        PlusEntry.STANDS -> navigator.navigateToStands()
                        PlusEntry.PAYMENT -> navigator.navigateToPayment()
                        PlusEntry.ACCESS -> navigator.navigateToAccess()
                        PlusEntry.ACCESSIBILITY -> navigator.navigateToAccessibility()
                        PlusEntry.HOURS -> navigator.navigateToHours()
                        PlusEntry.ASSISTANCE -> navigator.navigateToAssistance()
                        PlusEntry.FAQ -> navigator.navigateToFaq()
                    }
                },
            )
        }

        entry<StandsDestination> {
            StandsRoute(
                onNavigateBack = { navigator.navigateBack() },
                onNavigateToHappening = { id -> navigator.navigateToHappening(id) },
            )
        }

        entry<PaymentDestination> { PaymentRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AccessDestination> { AccessRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AccessibilityDestination> { AccessibilityRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<HoursDestination> { HoursRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AssistanceDestination> { AssistanceRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<FaqDestination> { FaqRoute(onNavigateBack = { navigator.navigateBack() }) }
    }
}
