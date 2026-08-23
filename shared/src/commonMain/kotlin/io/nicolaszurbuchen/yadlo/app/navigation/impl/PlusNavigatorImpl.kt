package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AboutDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AssistanceDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ClearDataDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ContactDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.FaqDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.HoursDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.NotificationsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PartnersDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PaymentDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PlusNavigator
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PrivacyDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ResponsibleDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StoryDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.VolunteeringDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class PlusNavigatorImpl(
    private val appNavigator: AppNavigator,
) : PlusNavigator {
    override fun navigateToStands(kind: StandsKindUiModel) {
        appNavigator.navigateTo(StandsDestination(kind))
    }

    override fun navigateToPayment() {
        appNavigator.navigateTo(PaymentDestination)
    }

    override fun navigateToAccess() {
        appNavigator.navigateTo(AccessDestination)
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

    override fun navigateToStory() {
        appNavigator.navigateTo(StoryDestination)
    }

    override fun navigateToPartners() {
        appNavigator.navigateTo(PartnersDestination)
    }

    override fun navigateToContact() {
        appNavigator.navigateTo(ContactDestination)
    }

    override fun navigateToVolunteering() {
        appNavigator.navigateTo(VolunteeringDestination)
    }

    override fun navigateToResponsible() {
        appNavigator.navigateTo(ResponsibleDestination)
    }

    override fun navigateToNotifications() {
        appNavigator.navigateTo(NotificationsDestination)
    }

    override fun navigateToAbout() {
        appNavigator.navigateTo(AboutDestination)
    }

    override fun navigateToPrivacy() {
        appNavigator.navigateTo(PrivacyDestination)
    }

    override fun navigateToClearData() {
        appNavigator.navigateTo(ClearDataDestination)
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
