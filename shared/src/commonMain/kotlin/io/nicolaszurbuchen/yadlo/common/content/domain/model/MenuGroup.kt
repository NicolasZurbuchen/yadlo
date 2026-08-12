package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A named section of a Stand's menu — plats, boissons, menus.
 *
 * [source] records where the prices came from in the author's own words, because no menu in the
 * content is confirmed by the festival: one is a vendor's own carte for a different location, one
 * was read off a photograph of a handwritten chalkboard. That distinction belongs next to the
 * prices rather than in a document nobody ships.
 */
data class MenuGroup(
    val id: String,
    val name: String,
    val source: String?,
    val items: List<Item>,
) {
    /**
     * Only [name] and [price] are required. An item with nothing else is a complete item, which
     * matters because that is the data most trucks will actually give.
     *
     * [marks] here describe this item alone. A Mark on the Stand describes the whole stand, and the
     * level is the meaning: "this stand is entirely vegan" is a different claim from "this stand has
     * a vegan option", and that is the actual question someone scanning a row of trucks is asking.
     */
    data class Item(
        val name: String,
        val price: Money?,
        val description: String?,
        val marks: List<String>,
        val provenance: Provenance,
    )
}
