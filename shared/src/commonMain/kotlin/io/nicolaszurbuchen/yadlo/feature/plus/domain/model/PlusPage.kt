package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink

/**
 * The shared shape behind the Plus entries that are a title, some prose, and somewhere to go.
 *
 * Most of this tab is that. A charter, the networks, and whatever the association publishes next
 * are the same page with different words in it, so they are one screen rather than a folder of
 * near-identical ones — the prototype's own conclusion after drawing them.
 *
 * What it is deliberately **not** is a layout language. There is no ordering field, no block type
 * and no styling: a [Section] is a heading, a paragraph and some links, and an entry that needs
 * more than that has earned a screen of its own. Horaires and Paiement both did.
 */
data class PlusPage(
    val sections: List<Section>,
) {
    data class Section(
        val id: String,
        /** Null on a page whose title above it says everything — a privacy statement has one voice. */
        val title: String?,
        val body: String?,
        val links: List<InfoLink>,
    )
}
