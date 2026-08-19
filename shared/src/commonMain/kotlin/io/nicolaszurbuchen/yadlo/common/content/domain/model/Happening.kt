package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A thing the festival offers. Sealed into three variants that share identity and differ only in
 * their detail payload, which is why one fiche template renders all three.
 *
 * [category] is resolved rather than an id, so no screen repeats the lookup or invents its own
 * answer for an id the Edition does not declare.
 *
 * **Never give this a `slots` list.** [Slot] already holds a Happening, so the back-reference would
 * make the graph cyclic, and a cyclic data class blows the stack on equals, hashCode and toString.
 * A fiche needs both and gets an aggregate assembled by a UseCase.
 */
sealed class Happening {
    abstract val id: String
    abstract val name: String
    abstract val category: Category
    abstract val description: String?
    abstract val images: List<Image>
    abstract val provenance: Provenance

    data class Artist(
        override val id: String,
        override val name: String,
        override val category: Category,
        override val description: String?,
        override val images: List<Image>,
        override val provenance: Provenance,
        val genres: List<String>,
        val links: List<Link>,
    ) : Happening()

    /**
     * The line between an Activity and a [Stand] is whether the festival programmed it: an Activity
     * has hours the organisers set, a Stand is simply there while the site is open.
     */
    data class Activity(
        override val id: String,
        override val name: String,
        override val category: Category,
        override val description: String?,
        override val images: List<Image>,
        override val provenance: Provenance,
        val genres: List<String>,
        val price: Price?,
        val bookingRequired: Boolean,
        val bookingUrl: String?,
        val equipmentProvided: Boolean?,
        /** Prose, not a minimum age: the one activity that states a limit states two at once. */
        val suitability: String?,
        /** Whether volunteers watch over it, which is what lets a parent leave a child there. */
        val supervised: Boolean?,
    ) : Happening()

    /**
     * **No marks of its own.** What a Stand can feed you is derived from its menu — see
     * [dietaryCoverage] — because a list authored here beside the lists on its items was two
     * levels that could contradict each other, and could not say "has a vegan option" at all.
     */
    data class Stand(
        override val id: String,
        override val name: String,
        override val category: Category,
        override val description: String?,
        override val images: List<Image>,
        override val provenance: Provenance,
        val offering: String?,
        val links: List<Link>,
        val menu: List<MenuGroup>,
    ) : Happening()
}
