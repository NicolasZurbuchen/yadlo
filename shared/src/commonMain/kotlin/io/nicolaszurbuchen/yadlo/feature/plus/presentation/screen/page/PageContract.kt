package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPage
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPageId

sealed interface PageIntent {
    data class LinkClicked(
        val url: String,
    ) : PageIntent
}

sealed interface PageLabel {
    data class OpenUrl(
        val url: String,
    ) : PageLabel
}

sealed interface PageAction {
    data object ObservePage : PageAction
}

sealed interface PageMessage {
    data class PageUpdated(
        val page: PlusPage,
    ) : PageMessage
}

/**
 * [kind] is in the state rather than only in the store's constructor because the screen's title
 * comes from it, and the title is the one thing this page cannot get from the content: *Festival
 * responsable* and *Réseaux sociaux* are app words, and the sections under them are the
 * association's. It is the presentation mirror of [PlusPageId], translated once by the store.
 */
data class PageState(
    val kind: PageKindUiModel,
    val page: PlusPage? = null,
)
