package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter

sealed interface ContactIntent {
    data class EmailClicked(
        val address: String,
    ) : ContactIntent
}

sealed interface ContactLabel {
    data class OpenUrl(
        val url: String,
    ) : ContactLabel
}

sealed interface ContactAction {
    data object ObserveRouter : ContactAction
}

sealed interface ContactMessage {
    data class RouterUpdated(
        val router: ContactRouter?,
    ) : ContactMessage
}

/**
 * [hasLoaded] and a nullable [router] are two facts, not one: the bundle has not landed yet, versus
 * it landed with no contact block in it. The first is a screen still filling in, the second is a
 * screen that says so.
 */
data class ContactState(
    val router: ContactRouter? = null,
    val hasLoaded: Boolean = false,
)
