package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import io.nicolaszurbuchen.yadlo.infra.text.UiText

/**
 * *Festival responsable* — the charters the association has signed, one section each.
 *
 * This was a parameterised gabarit while *Réseaux sociaux* shared the same shape: a `kind` enum
 * decided the title, the destination carried it through process death and the store translated it
 * into a domain id. The networks became the foot of the tab, which left the whole apparatus with one
 * possible value, and machinery that can only be given one argument is machinery that has stopped
 * being a decision. It is one screen about one thing now, and the shape can be generalised again the
 * day there is a second page to generalise it *for*.
 *
 * The title is not on the model for the same reason: it is a fixed app string, and a screen that
 * only ever draws one heading should say which one in its own source.
 */
data class ResponsibleUiModel(
    val isLoading: Boolean,
    val sections: List<ResponsibleSectionUiModel>,
    val emptyMessage: UiText?,
)

data class ResponsibleSectionUiModel(
    val id: String,
    val title: String,
    val body: String?,
    val links: List<ResponsibleLinkUiModel>,
)

data class ResponsibleLinkUiModel(
    val id: String,
    val label: String,
    val sublabel: String?,
    val url: String,
)
