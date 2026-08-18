package io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel

/**
 * One address out of the association's directory, as it is offered to be written to.
 *
 * Shared by *Nous écrire* and *Devenir Hot'Staff* rather than described twice, because they are
 * offering the same thing and were not saying so. Volunteering used to put the bare address on the
 * tile — `staff@yadlo.ch` and nothing else — so the one screen actively recruiting was the one that
 * looked like a `mailto:` somebody had pasted in, while the router two rows away named the concern
 * and the person behind it.
 *
 * [label] is what the address is for, in the association's words. [responsible] is who is behind
 * it, and is null on the general address, which has nobody in particular behind it by design.
 */
data class PlusEmailUiModel(
    val id: String,
    val label: String,
    val responsible: String?,
    val address: String,
)
