package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsShortDate

fun AnnouncementsState.toUiModel(): AnnouncementsUiModel =
    AnnouncementsUiModel(
        isLoading = isLoading,
        items =
            announcements.map { announcement ->
                AnnouncementUiModel(
                    id = announcement.id,
                    dateText = announcement.publishedAt.formatAsShortDate(FESTIVAL_TIME_ZONE),
                    title = announcement.title,
                    body = announcement.body.orEmpty(),
                    url = announcement.url,
                )
            },
    )
