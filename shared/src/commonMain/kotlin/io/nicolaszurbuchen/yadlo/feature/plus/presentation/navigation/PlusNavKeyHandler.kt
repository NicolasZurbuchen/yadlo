package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about.AboutRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility.AccessibilityRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.ContactRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageKind
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnersRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusEntry
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.privacy.PrivacyRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKind
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.StoryRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class PlusNavKeyHandler(
    private val navigator: PlusNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        // The root hands back which row was tapped rather than a destination, because a Route may
        // only take lambdas and the screen has no business naming a NavKey. This `when` is the one
        // place the tab's rows and the app's back stack meet. The two external rows never arrive
        // here — the Route sends those to the store, which publishes a URL for the platform.
        entry<PlusMainDestination> {
            PlusRoute(
                onNavigateToEntry = { entry ->
                    when (entry) {
                        PlusEntry.STANDS_FOOD -> navigator.navigateToStands(StandsKind.FOOD)
                        PlusEntry.STANDS_MAKERS -> navigator.navigateToStands(StandsKind.MAKERS)
                        PlusEntry.PAYMENT -> navigator.navigateToPayment()
                        PlusEntry.ACCESS -> navigator.navigateToAccess()
                        PlusEntry.ACCESSIBILITY -> navigator.navigateToAccessibility()
                        PlusEntry.HOURS -> navigator.navigateToHours()
                        PlusEntry.ASSISTANCE -> navigator.navigateToAssistance()
                        PlusEntry.FAQ -> navigator.navigateToFaq()
                        PlusEntry.STORY -> navigator.navigateToStory()
                        PlusEntry.RESPONSIBLE -> navigator.navigateToPage(PageKind.RESPONSIBLE)
                        PlusEntry.PARTNERS -> navigator.navigateToPartners()
                        PlusEntry.CONTACT -> navigator.navigateToContact()
                        PlusEntry.SOCIAL -> navigator.navigateToPage(PageKind.SOCIAL)
                        PlusEntry.ABOUT -> navigator.navigateToAbout()
                        PlusEntry.PRIVACY -> navigator.navigateToPrivacy()
                        PlusEntry.NEWSLETTER, PlusEntry.REPORT -> Unit
                    }
                },
            )
        }

        entry<StandsDestination> { destination ->
            StandsRoute(
                onNavigateBack = { navigator.navigateBack() },
                onNavigateToHappening = { id -> navigator.navigateToHappening(id) },
                viewModel = koinViewModel<StandsViewModel>(parameters = { parametersOf(destination.kind) }),
            )
        }

        // Which page it is reaches the store through the ViewModel, the same construction-parameter
        // route the fiche's Happening id travels. The destination survives process death, so a
        // restored back stack rebuilds the page it was on.
        entry<PageDestination> { destination ->
            PageRoute(
                onNavigateBack = { navigator.navigateBack() },
                viewModel = koinViewModel<PageViewModel>(parameters = { parametersOf(destination.kind) }),
            )
        }

        entry<PaymentDestination> { PaymentRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AccessDestination> { AccessRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AccessibilityDestination> { AccessibilityRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<HoursDestination> { HoursRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AssistanceDestination> { AssistanceRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<FaqDestination> { FaqRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<StoryDestination> { StoryRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<PartnersDestination> { PartnersRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<ContactDestination> { ContactRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AboutDestination> { AboutRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<PrivacyDestination> { PrivacyRoute(onNavigateBack = { navigator.navigateBack() }) }
    }
}
