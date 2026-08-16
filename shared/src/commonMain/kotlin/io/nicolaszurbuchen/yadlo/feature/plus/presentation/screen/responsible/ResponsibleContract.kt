package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ResponsiblePage

sealed interface ResponsibleIntent {
    data class LinkClicked(
        val url: String,
    ) : ResponsibleIntent
}

sealed interface ResponsibleLabel {
    data class OpenUrl(
        val url: String,
    ) : ResponsibleLabel
}

sealed interface ResponsibleAction {
    data object ObservePage : ResponsibleAction
}

sealed interface ResponsibleMessage {
    data class PageUpdated(
        val page: ResponsiblePage,
    ) : ResponsibleMessage
}

/** A null [page] is the bundle not having landed yet. */
data class ResponsibleState(
    val page: ResponsiblePage? = null,
)
