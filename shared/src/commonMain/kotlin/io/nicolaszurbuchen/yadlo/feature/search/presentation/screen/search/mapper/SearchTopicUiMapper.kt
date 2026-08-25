package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.mapper

import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchTopicUiModel

/**
 * The matched topic, given the title and the icon the Plus tab already wears for it.
 *
 * It ran in the Store before, so that the State could carry the presentation twin beside the
 * results. That made the State hold the same list twice — `results.topics` and a converted copy of
 * it — and a Store that converts is a Store doing the mapper's work. `results` is the one source
 * now, and this is the only step between it and the row.
 */
fun SearchTopic.toUiModel(): SearchTopicUiModel =
    when (this) {
        SearchTopic.STANDS_FOOD -> SearchTopicUiModel.STANDS_FOOD
        SearchTopic.STANDS_MAKERS -> SearchTopicUiModel.STANDS_MAKERS
        SearchTopic.PAYMENT -> SearchTopicUiModel.PAYMENT
        SearchTopic.ACCESS -> SearchTopicUiModel.ACCESS
        SearchTopic.HOURS -> SearchTopicUiModel.HOURS
        SearchTopic.ASSISTANCE -> SearchTopicUiModel.ASSISTANCE
        SearchTopic.FAQ -> SearchTopicUiModel.FAQ
        SearchTopic.STORY -> SearchTopicUiModel.STORY
        SearchTopic.RESPONSIBLE -> SearchTopicUiModel.RESPONSIBLE
        SearchTopic.PARTNERS -> SearchTopicUiModel.PARTNERS
        SearchTopic.VOLUNTEERING -> SearchTopicUiModel.VOLUNTEERING
        SearchTopic.CONTACT -> SearchTopicUiModel.CONTACT
        SearchTopic.NOTIFICATIONS -> SearchTopicUiModel.NOTIFICATIONS
        SearchTopic.PRIVACY -> SearchTopicUiModel.PRIVACY
        SearchTopic.ABOUT -> SearchTopicUiModel.ABOUT
    }
