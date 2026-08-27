package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Announcement

sealed interface AnnouncementsIntent {
    data class AnnouncementClicked(
        val url: String,
    ) : AnnouncementsIntent
}

sealed interface AnnouncementsLabel {
    data class OpenUrl(
        val url: String,
    ) : AnnouncementsLabel
}

sealed interface AnnouncementsAction {
    data object ObserveContent : AnnouncementsAction
}

sealed interface AnnouncementsMessage {
    data class AnnouncementsUpdated(
        val announcements: List<Announcement>,
    ) : AnnouncementsMessage
}

/**
 * No phase and no clock here. Accueil narrows the feed to the last day during LIVE because that is
 * a summary; this screen is the record, so it shows everything the current edition has published.
 */
data class AnnouncementsState(
    val isLoading: Boolean = true,
    val announcements: List<Announcement> = emptyList(),
)
