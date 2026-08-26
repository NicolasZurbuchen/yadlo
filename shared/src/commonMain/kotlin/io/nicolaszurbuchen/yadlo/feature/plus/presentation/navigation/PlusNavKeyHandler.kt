package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about.AboutRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata.ClearDataRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.ContactRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications.NotificationsRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnersRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusEntryUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.privacy.PrivacyRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible.ResponsibleRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.StoryRoute
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering.VolunteeringRoute
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
                        PlusEntryUiModel.STANDS_FOOD -> navigator.navigateToFoodStands()
                        PlusEntryUiModel.STANDS_MAKERS -> navigator.navigateToMakerStands()
                        PlusEntryUiModel.PAYMENT -> navigator.navigateToPayment()
                        PlusEntryUiModel.ACCESS -> navigator.navigateToAccess()
                        PlusEntryUiModel.HOURS -> navigator.navigateToHours()
                        PlusEntryUiModel.ASSISTANCE -> navigator.navigateToAssistance()
                        PlusEntryUiModel.FAQ -> navigator.navigateToFaq()
                        PlusEntryUiModel.STORY -> navigator.navigateToStory()
                        PlusEntryUiModel.RESPONSIBLE -> navigator.navigateToResponsible()
                        PlusEntryUiModel.PARTNERS -> navigator.navigateToPartners()
                        PlusEntryUiModel.CONTACT -> navigator.navigateToContact()
                        PlusEntryUiModel.VOLUNTEERING -> navigator.navigateToVolunteering()
                        PlusEntryUiModel.NOTIFICATIONS -> navigator.navigateToNotifications()
                        PlusEntryUiModel.ABOUT -> navigator.navigateToAbout()
                        PlusEntryUiModel.PRIVACY -> navigator.navigateToPrivacy()
                        PlusEntryUiModel.CLEAR_DATA -> navigator.navigateToClearData()
                        PlusEntryUiModel.NEWSLETTER, PlusEntryUiModel.REPORT -> Unit
                    }
                },
            )
        }

        // The kind is handed to the Store here rather than carried by the key. It reaches the
        // Store as the presentation mirror because navigation may not name a domain type, and
        // the Store translates once at construction — but it goes no further than this call, so
        // nothing writes it down.
        entry<StandsFoodDestination> { StandsEntry(kind = StandsKindUiModel.FOOD, navigator = navigator) }
        entry<StandsMakersDestination> { StandsEntry(kind = StandsKindUiModel.MAKERS, navigator = navigator) }

        entry<ResponsibleDestination> { ResponsibleRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<PaymentDestination> { PaymentRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AccessDestination> { AccessRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<HoursDestination> { HoursRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AssistanceDestination> { AssistanceRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<FaqDestination> { FaqRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<StoryDestination> { StoryRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<PartnersDestination> { PartnersRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<ContactDestination> { ContactRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<VolunteeringDestination> { VolunteeringRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<AboutDestination> { AboutRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<NotificationsDestination> { NotificationsRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<PrivacyDestination> { PrivacyRoute(onNavigateBack = { navigator.navigateBack() }) }
        entry<ClearDataDestination> { ClearDataRoute(onNavigateBack = { navigator.navigateBack() }) }
    }
}

/**
 * The one screen both stands keys open, told which half it is.
 *
 * A function rather than the same four lines twice: the two entries differ by one argument, and a
 * pair that has to be kept in step by hand is how one of them ends up wired to the wrong half.
 */
@Composable
private fun StandsEntry(
    kind: StandsKindUiModel,
    navigator: PlusNavigator,
) {
    StandsRoute(
        onNavigateBack = { navigator.navigateBack() },
        onNavigateToHappening = { id -> navigator.navigateToHappening(id) },
        viewModel = koinViewModel<StandsViewModel>(parameters = { parametersOf(kind) }),
    )
}
