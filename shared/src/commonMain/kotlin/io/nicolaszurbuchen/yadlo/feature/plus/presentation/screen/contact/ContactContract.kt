package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter

sealed interface ContactIntent {
    data class EmailClicked(
        val address: String,
    ) : ContactIntent

    data class SignupClicked(
        val url: String,
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

data class ContactState(
    val router: ContactRouter? = null,
    val hasLoaded: Boolean = false,
)
