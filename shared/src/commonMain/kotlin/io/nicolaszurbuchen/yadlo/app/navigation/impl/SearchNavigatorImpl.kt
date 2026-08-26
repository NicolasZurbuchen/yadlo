package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation.HappeningDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AboutDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AssistanceDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ContactDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.FaqDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.HoursDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.NotificationsDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PartnersDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PaymentDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PrivacyDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.ResponsibleDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsFoodDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StandsMakersDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StoryDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.VolunteeringDestination
import io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation.SearchNavigator
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

/**
 * The one file that knows a search result about TWINT is a screen the Plus tab owns.
 *
 * Pushes rather than tab switches, for the reason `SearchDestination` gives: the search itself was
 * pushed onto whichever tab was showing, so everything opened from it stacks there too and backs out
 * the way it came. A reader who searched from the Programme and read the payment page returns to
 * their results, then to the Programme.
 */
class SearchNavigatorImpl(
    private val appNavigator: AppNavigator,
) : SearchNavigator {
    override fun navigateToHappening(happeningId: String) {
        appNavigator.navigateTo(HappeningDestination(happeningId))
    }

    override fun navigateToTopic(topic: SearchTopicUiModel) {
        // Exhaustive over the enum, so a topic added without a destination is a compile error
        // rather than a result that does nothing when it is tapped.
        val destination =
            when (topic) {
                SearchTopicUiModel.STANDS_FOOD -> StandsFoodDestination
                SearchTopicUiModel.STANDS_MAKERS -> StandsMakersDestination
                SearchTopicUiModel.PAYMENT -> PaymentDestination
                SearchTopicUiModel.ACCESS -> AccessDestination
                SearchTopicUiModel.HOURS -> HoursDestination
                SearchTopicUiModel.ASSISTANCE -> AssistanceDestination
                SearchTopicUiModel.FAQ -> FaqDestination
                SearchTopicUiModel.STORY -> StoryDestination
                SearchTopicUiModel.RESPONSIBLE -> ResponsibleDestination
                SearchTopicUiModel.PARTNERS -> PartnersDestination
                SearchTopicUiModel.VOLUNTEERING -> VolunteeringDestination
                SearchTopicUiModel.CONTACT -> ContactDestination
                SearchTopicUiModel.NOTIFICATIONS -> NotificationsDestination
                SearchTopicUiModel.PRIVACY -> PrivacyDestination
                SearchTopicUiModel.ABOUT -> AboutDestination
            }

        appNavigator.navigateTo(destination)
    }

    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
