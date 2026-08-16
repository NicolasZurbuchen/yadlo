package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * What the site takes, and what it does not. The single most consequential practical fact the
 * festival publishes, and the one it buries deepest: **carte et TWINT uniquement, pas d'espèces**.
 *
 * It is only actionable *before leaving the house*, which is why it sits third on the Plus tab and
 * is borrowed onto Accueil from J-7 — someone reading it on the beach has already walked past the
 * cash machine they needed.
 *
 * [headline] and [summary] are that fact said in three words and in one sentence, because a reader
 * who opens this screen has one question and should not have to assemble the answer from a list.
 * The list is still under them, for the reader who has a Maestro card and wants to be sure.
 */
data class Payment(
    val headline: String?,
    val summary: String?,
    val methods: List<Method>,
    val notes: List<Note>,
    val provenance: Provenance,
) {
    /**
     * **[accepted] is a boolean, never "unknown".** A method nobody has confirmed is left out of
     * this list entirely rather than rendered as a shrug, because "TWINT: ?" helps no one.
     */
    data class Method(
        val id: String,
        val name: String,
        val accepted: Boolean,
    )

    /**
     * Everything the list of methods cannot say in a boolean, under a heading of its own.
     *
     * [links] belong to the note rather than to the block, which is the only arrangement that puts
     * twint.ch under *Vous n'avez pas TWINT ?* instead of in a bin of links at the foot of the page.
     * The same shape `ResponsiblePage.Section` already has, for the same reason.
     */
    data class Note(
        val id: String,
        val title: String,
        val body: String,
        val links: List<InfoLink>,
    )
}
