package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * What the site takes, and what it does not. The single most consequential practical fact the
 * festival publishes, and the one it buries deepest: **carte et TWINT uniquement, pas d'espèces**.
 *
 * It is only actionable *before leaving the house*, which is why it sits third on the Plus tab and
 * is borrowed onto Accueil from J-7 — someone reading it on the beach has already walked past the
 * cash machine they needed.
 */
data class Payment(
    val methods: List<Method>,
    val notes: List<Note>,
    val links: List<InfoLink>,
    val provenance: Provenance,
) {
    /**
     * **[accepted] is a boolean, never "unknown".** A method nobody has confirmed is left out of
     * this list entirely rather than rendered as a shrug, because "TWINT: ?" helps no one. That is
     * why the contactless wallets are not here: they almost certainly work wherever the cards do,
     * and "almost certainly" is not what a list of accepted methods claims. They are stated in a
     * [Note] instead, which is free text and does not pretend to be official.
     */
    data class Method(
        val id: String,
        val name: String,
        val accepted: Boolean,
    )

    /** Everything the list of methods cannot say in a boolean. */
    data class Note(
        val id: String,
        val body: String,
    )
}
