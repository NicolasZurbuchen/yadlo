package io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel

/**
 * One annonce, as both the Accueil block and the full list render it — they show the same card, so
 * they share the model rather than each keeping their own copy of what a card needs.
 *
 * [body] is empty rather than null when the organisers wrote none: absent and blank are the same
 * thing to a reader, and the card has one less state to have an opinion about.
 *
 * [url] stays nullable because it is not the same kind of absence: story 85 wants an annonce with
 * nowhere to go plainly untappable, which is a decision the card has to make.
 */
data class AnnouncementUiModel(
    val id: String,
    val dateText: String,
    val title: String,
    val body: String,
    val url: String?,
)
