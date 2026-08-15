package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Involvement

/**
 * *Nous écrire* — an aiguillage, not a form.
 *
 * **No backend, no stored messages, and no becoming a data processor.** Every choice opens
 * something that already exists: the association's own recruitment site for volunteers, and the
 * visitor's own mail app for everything else. That is not only simpler — it keeps their existing
 * pipeline receiving its applications instead of routing them through a personal inbox that has to
 * forward them by hand during the busiest month of their year.
 *
 * [emails] is the directory as published, each with the label the association wrote for it. Nine
 * addresses is more than a picker of four concerns, and choosing which four would be guessing at
 * their internal division of labour.
 */
data class ContactRouter(
    val volunteering: Involvement.Volunteering?,
    /** Resolved from [Involvement.Volunteering.contactEmailId], for what a signup page cannot answer. */
    val volunteeringEmail: String?,
    val emails: List<Contact.Email>,
    val addressLines: List<String>,
)
