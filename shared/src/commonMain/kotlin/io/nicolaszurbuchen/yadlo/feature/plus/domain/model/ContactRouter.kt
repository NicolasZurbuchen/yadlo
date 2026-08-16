package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact

/**
 * *Nous écrire* — an aiguillage, not a form.
 *
 * **No backend, no stored messages, and no becoming a data processor.** Every choice opens the
 * visitor's own mail app against an address the association already publishes. That is not only
 * simpler — it keeps their existing inboxes receiving their own mail instead of routing it through
 * a personal one that has to forward it by hand during the busiest month of their year.
 *
 * [emails] is the directory as published, each with the label the association wrote for it. Nine
 * addresses is more than a picker of four concerns, and choosing which four would be guessing at
 * their internal division of labour.
 *
 * Recruiting is deliberately **not** here — see [VolunteeringOffer]. It is a campaign with a page
 * of its own, and the one thing in *S'impliquer* nobody should have to open a mail router to find.
 */
data class ContactRouter(
    val emails: List<Contact.Email>,
    val addressLines: List<String>,
)
