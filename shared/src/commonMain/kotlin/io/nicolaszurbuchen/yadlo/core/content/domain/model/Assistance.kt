package io.nicolaszurbuchen.yadlo.core.content.domain.model

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
    /**
     * How to tell who works here — the t-shirt, and how many of them are about.
     *
     * Content rather than a string in the app, because "160" is a fact about one edition and the
     * app has no business asserting it. Authored here so the association can correct it without a
     * release, and so it is not silently derived from an archived figure.
     */
    val recognition: List<Recognition>,
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

    data class Recognition(
        val id: String,
        val text: String,
    )
}
