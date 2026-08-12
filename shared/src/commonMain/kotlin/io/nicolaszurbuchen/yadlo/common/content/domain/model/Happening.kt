package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A thing the festival offers, with its own identity, description, images and detail screen. One
 * Happening has one or more Slots.
 *
 * Sealed into three variants that share identity and differ only in their detail payload, which is
 * exactly why one fiche template renders all three.
 *
 * [categoryId] rather than a [Category] because a Happening is authored against a category id and the
 * label lives on the Edition. Resolving it here would mean every Happening carrying a copy of the
 * same label, and a Happening whose category is missing from the edition failing to construct rather
 * than rendering with a neutral chip.
 */
sealed class Happening {
    abstract val id: String
    abstract val name: String
    abstract val categoryId: String
    abstract val description: String?
    abstract val images: List<Image>
    abstract val provenance: Provenance

    /** A musical act. */
    data class Artist(
        override val id: String,
        override val name: String,
        override val categoryId: String,
        override val description: String?,
        override val images: List<Image>,
        override val provenance: Provenance,
        val genres: List<String>,
        val links: List<Link>,
    ) : Happening()

    /**
     * Something to do rather than watch — sport, games, wellness, children's offerings.
     *
     * The line between an Activity and a [Stand] is whether the festival programmed it: an Activity
     * has hours the organisers set and publish, a Stand is simply there while the site is open.
     */
    data class Activity(
        override val id: String,
        override val name: String,
        override val categoryId: String,
        override val description: String?,
        override val images: List<Image>,
        override val provenance: Provenance,
        val genres: List<String>,
        val price: Price?,
        val bookingRequired: Boolean,
        val bookingUrl: String?,
        val equipmentProvided: Boolean?,
        val minimumAge: Int?,
    ) : Happening()

    /**
     * Somewhere present on the site that you visit rather than attend — food trucks, the bar,
     * clothing and craft sellers. Its Slots are opening windows.
     *
     * [marks] describe the whole stand: Vegan Fabrik is vegan and bio because everything it sells is.
     * A stand-level mark must never be repeated on every one of its items.
     */
    data class Stand(
        override val id: String,
        override val name: String,
        override val categoryId: String,
        override val description: String?,
        override val images: List<Image>,
        override val provenance: Provenance,
        val offering: String?,
        val marks: List<String>,
        val links: List<Link>,
        val menu: List<MenuGroup>,
    ) : Happening()
}
