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

/**
 * The way back, for the one value on this screen that makes a round trip.
 *
 * A practical row is drawn from a [SearchTopicUiModel] — it is what carries the title and the icon —
 * and a tap hands that same value back. What the Store should hear is the topic, not the drawing of
 * it, so the Route converts here rather than the Contract naming a twin it has no use for.
 *
 * The pairing is by name in both directions and `SearchTopicUiMapperTest` holds it to that, so
 * neither enum can grow a constant the other does not have.
 */
fun SearchTopicUiModel.toDomain(): SearchTopic =
    when (this) {
        SearchTopicUiModel.STANDS_FOOD -> SearchTopic.STANDS_FOOD
        SearchTopicUiModel.STANDS_MAKERS -> SearchTopic.STANDS_MAKERS
        SearchTopicUiModel.PAYMENT -> SearchTopic.PAYMENT
        SearchTopicUiModel.ACCESS -> SearchTopic.ACCESS
        SearchTopicUiModel.HOURS -> SearchTopic.HOURS
        SearchTopicUiModel.ASSISTANCE -> SearchTopic.ASSISTANCE
        SearchTopicUiModel.FAQ -> SearchTopic.FAQ
        SearchTopicUiModel.STORY -> SearchTopic.STORY
        SearchTopicUiModel.RESPONSIBLE -> SearchTopic.RESPONSIBLE
        SearchTopicUiModel.PARTNERS -> SearchTopic.PARTNERS
        SearchTopicUiModel.VOLUNTEERING -> SearchTopic.VOLUNTEERING
        SearchTopicUiModel.CONTACT -> SearchTopic.CONTACT
        SearchTopicUiModel.NOTIFICATIONS -> SearchTopic.NOTIFICATIONS
        SearchTopicUiModel.PRIVACY -> SearchTopic.PRIVACY
        SearchTopicUiModel.ABOUT -> SearchTopic.ABOUT
    }
