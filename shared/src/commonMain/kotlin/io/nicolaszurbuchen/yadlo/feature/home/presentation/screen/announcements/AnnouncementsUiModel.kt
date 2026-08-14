package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel

data class AnnouncementsUiModel(
    val isLoading: Boolean,
    val items: List<AnnouncementUiModel>,
)
