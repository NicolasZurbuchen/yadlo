package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * The two ways of joining in — giving hours, or backing the festival.
 *
 * Two named fields rather than a list, because they are genuinely different offers and the screen
 * treats them differently: volunteering goes to the association's own recruitment site, which is
 * where applications are already handled, and partnership opens a mail. A third way of joining in
 * would be a new field and a new block, not a row that appeared in a loop.
 */
data class Involvement(
    val volunteering: Volunteering?,
    val partnership: Partnership?,
) {
    /**
     * *Hot'Staff.* [signupUrl] is the association's own recruitment site rather than a form in the
     * app: their pipeline keeps receiving its applications instead of landing in a personal inbox
     * that has to forward them by hand during the busiest month of their year.
     */
    data class Volunteering(
        val name: String,
        val body: String,
        val perks: List<String>,
        val signupUrl: String?,
        /** Into [Contact.emails], for the questions a signup form does not answer. */
        val contactEmailId: String,
        val provenance: Provenance,
    )

    /** *Devenir partenaire.* Reached from the foot of the Partners screen, never as its sibling. */
    data class Partnership(
        val name: String,
        val body: String?,
        val contactEmailId: String,
        val provenance: Provenance,
    )
}
