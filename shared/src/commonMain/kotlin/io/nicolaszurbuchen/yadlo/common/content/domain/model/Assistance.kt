package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * *En cas de besoin* — first aid, lost property and lost children, merged into one screen.
 *
 * The merge is justified by a shared situation rather than by tidiness: everything here answers
 * "something has gone wrong", which is a different mode from browsing a tab of practical
 * information. Three short entries would also have been three short entries; that is not the reason.
 *
 * [emergencyNumbers] are the only content in this app that are true whatever happens and need
 * nobody's confirmation, which is why they are first.
 */
data class Assistance(
    val emergencyNumbers: List<EmergencyNumber>,
    /** Into [Contact.emails] — where a bag goes once the festival has packed up. */
    val lostPropertyEmailId: String,
    val provenance: Provenance,
) {
    data class EmergencyNumber(
        val id: String,
        val label: String,
        /** Kept as text: these are dialled, never computed, and 144 is not the number 144. */
        val number: String,
    )
}
