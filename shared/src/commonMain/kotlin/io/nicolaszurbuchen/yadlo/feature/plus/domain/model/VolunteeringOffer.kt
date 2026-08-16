package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * *Hot'Staff* — what the association asks of a volunteer and what it offers in return, on a screen
 * of its own.
 *
 * **Split out of the contact router, not duplicated from it.** Recruiting is the one thing in
 * *S'impliquer* the festival is actively campaigning for, and it was reachable only through *Nous
 * écrire* — a row somebody opens when they already know they want to send an email. A screen behind
 * its own row is what the website does with it too, and for the same reason.
 *
 * [email] is resolved here rather than carried as an id, because it is the one thing on the screen
 * a signup page cannot answer and the screen has no way to look an id up. Null when the address the
 * content names is not in the directory — a content bug the screen renders as one fewer tile rather
 * than as a `mailto:` to nowhere.
 */
data class VolunteeringOffer(
    val name: String,
    val body: String,
    val perks: List<String>,
    val signupUrl: String?,
    val email: String?,
)
