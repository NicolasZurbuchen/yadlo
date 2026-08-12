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
        /**
         * Who the activity is for, in the content's own words — "De 4 à 12 ans, deux heures
         * maximum". Prose rather than a minimum age, because the one activity that states a limit
         * states two of them at once and neither is a number the app would do arithmetic on.
         */
        val suitability: String?,
        /** Whether volunteers watch over it, which is what lets a parent leave a child there. */
        val supervised: Boolean?,
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
