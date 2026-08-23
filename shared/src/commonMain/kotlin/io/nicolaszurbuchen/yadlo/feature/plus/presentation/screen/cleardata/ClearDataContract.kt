package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.SavedCount

sealed interface ClearDataIntent {
    /** Opens the question, never the deletion. Only [SavedConfirmed] removes anything. */
    data object SavedClicked : ClearDataIntent

    data object SavedConfirmed : ClearDataIntent

    data object SavedDismissed : ClearDataIntent

    /** No confirmation: every byte of it comes back off the network — see `ClearImageCacheUseCase`. */
    data object ImagesClicked : ClearDataIntent
}

/**
 * Empty, and it stays empty. Nothing here navigates, opens a URL or leaves the app: both actions
 * finish on this screen and their result is the numbers on it changing. Declared rather than
 * omitted, because that is what the shape says out loud.
 */
sealed interface ClearDataLabel

sealed interface ClearDataAction {
    data object ObserveSaved : ClearDataAction

    data object ReadImageCacheSize : ClearDataAction
}

sealed interface ClearDataMessage {
    data class SavedUpdated(
        val count: SavedCount,
    ) : ClearDataMessage

    data class ImageCacheSizeUpdated(
        val bytes: Long,
    ) : ClearDataMessage

    data class ConfirmationChanged(
        val isAsking: Boolean,
    ) : ClearDataMessage
}

/**
 * Null in either count is "not read yet", which is what keeps the screen from drawing *rien
 * d'enregistré* for a frame and then correcting itself — the one thing this screen must not do,
 * since a visitor who reads that has their answer and leaves.
 *
 * [isAskingAboutSaved] is a field rather than dialog state held in the composable so that the
 * question survives a rotation, and because a screen whose only two behaviours are "ask" and
 * "delete" should be readable in one place.
 */
data class ClearDataState(
    val savedCount: SavedCount? = null,
    val imageCacheBytes: Long? = null,
    val isAskingAboutSaved: Boolean = false,
)
