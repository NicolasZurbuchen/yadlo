package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * What is and is not accessible on a site that is mostly sand.
 *
 * [items] is empty today, and that is the honest state rather than a gap to paper over: the
 * festival publishes nothing on the subject. The screen is built around saying so and offering the
 * only genuinely useful thing left — somebody to write to before travelling.
 */
data class Accessibility(
    val items: List<Item>,
    /** Into [Contact.emails]. The screen is a dead end without it, so it is never a free address. */
    val contactEmailId: String,
    val provenance: Provenance,
) {
    /**
     * **Recording what is *not* available matters as much as what is.** "No accessible toilets" is
     * something a person needs before deciding to travel thirty kilometres; silence tells them
     * nothing, and a reassuringly vague page is worse than an honest list of unknowns.
     */
    data class Item(
        val id: String,
        val name: String,
        val available: Boolean,
        val note: String?,
    )
}
