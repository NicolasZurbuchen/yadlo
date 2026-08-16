package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * How to reach the association, and the only outbound channel the app has.
 *
 * **S'impliquer is a router, not a form.** Nothing here is posted anywhere: every address opens the
 * visitor's own mail app with a subject already filled in, so the association's existing pipeline
 * keeps receiving its mail and the app never becomes a data processor.
 *
 * [emails] is a list rather than nine named fields because the addresses are a directory the
 * association maintains — `musique@`, `foodtrucks@`, `staff@` — and a tenth should be a content
 * edit. Screens look one up by id ([Accessibility.contactEmailId], [Assistance.lostPropertyEmailId]).
 */
data class Contact(
    val addressLines: List<String>,
    val phone: String?,
    val emails: List<Email>,
    val provenance: Provenance,
) {
    data class Email(
        val id: String,
        val address: String,
        /** What this address is for, in the association's words — "Programmation musicale". */
        val label: String,
        /**
         * Who is behind it, when the association has said. Null on `hello@`, which is the general
         * address precisely because nobody in particular owns it — the one entry where a name would
         * be an invention rather than a fact.
         */
        val responsible: String?,
    )
}
