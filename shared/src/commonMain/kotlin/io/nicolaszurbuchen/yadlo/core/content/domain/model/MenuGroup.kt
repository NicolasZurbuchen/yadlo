package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * [source] records where the prices came from, because no menu here is confirmed by the festival —
 * one is a vendor's carte for another location, one was read off a photographed chalkboard.
 */
data class MenuGroup(
    val id: String,
    val name: String,
    /** What the group is when its name does not say: "Bokits" is a Guadeloupean fried sandwich. */
    val description: String?,
    val source: String?,
    val items: List<Item>,
) {
    /**
     * [marks] here describe this item alone; marks on the Stand describe everything it sells. That
     * is the difference between "entirely vegan" and "has a vegan option", which is the question
     * someone scanning a row of trucks is actually asking.
     */
    data class Item(
        val name: String,
        val price: Money?,
        val description: String?,
        val marks: List<String>,
        val provenance: Provenance,
    )
}
