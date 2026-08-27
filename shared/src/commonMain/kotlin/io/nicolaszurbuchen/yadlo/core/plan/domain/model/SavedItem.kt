package io.nicolaszurbuchen.yadlo.core.plan.domain.model

/**
 * One thing the visitor kept.
 *
 * [id] is a Slot id for [SavedKind.SLOT] and a Happening id for [SavedKind.STAND] — two id spaces
 * sharing one table, which is safe because nothing ever looks an id up without also knowing which
 * [kind] it is reading.
 *
 * [editionId] is carried so a saved thing can still be attributed to its year after the content has
 * moved on. Nothing reads it yet; the row is where it would otherwise be lost.
 */
data class SavedItem(
    val id: String,
    val kind: SavedKind,
    val editionId: String,
)
