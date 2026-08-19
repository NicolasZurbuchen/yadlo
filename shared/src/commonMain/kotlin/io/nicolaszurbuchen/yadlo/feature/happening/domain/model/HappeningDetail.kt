package io.nicolaszurbuchen.yadlo.feature.happening.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.DietaryCoverage
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Link
import io.nicolaszurbuchen.yadlo.common.content.domain.model.MenuGroup
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price

/**
 * A Happening and its Slots — the aggregate SPEC.md § Domain model promises the fiche, because
 * `Happening` may never carry a `slots` list without making the graph cyclic.
 *
 * **Flat, with the sealed variant already resolved.** One fiche template renders an Artist, an
 * Activity and a Stand, so somewhere the three payloads have to become one shape. That happens here
 * rather than in the UiMapper: branching on `Happening.Activity` means naming a domain type, and the
 * presentation layer is not allowed to import one. The same reasoning put `price` on
 * `ProgrammeSlot`.
 *
 * The fields an Activity alone carries are null on the other two. That is a real absence rather than
 * defensive nullability — an artist has no equipment to provide and no admission to charge.
 *
 * [tags] are attribute-only, per DECISIONS.md § One fiche template: genres for an Artist, the
 * offering and the marks for a Stand. Never the Category, which is written above the title, and
 * never the price, which cannot survive the shortening a tag imposes.
 *
 * [wishlisted] is the last place the variant still shows through, and it has to: a Stand is kept as
 * a whole and everything else is kept one Slot at a time, so the two carry their heart in different
 * places. Resolved to a nullable Boolean here for the same reason as everything above — the screen
 * asks "is there one heart or several", never "is this a Stand".
 */
data class HappeningDetail(
    val id: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val description: String?,
    val tags: List<String>,
    /**
     * What a Stand can feed you, derived from its menu. Not folded into [tags] because these carry a
     * glyph and a colour of their own and are read by someone who cannot eat what they get wrong —
     * a word in a row of genres is not that.
     */
    val dietary: Map<String, DietaryCoverage>,
    val slots: List<HappeningSlot>,
    val price: Price?,
    val bookingUrl: String?,
    val bookingRequired: Boolean,
    val equipmentProvided: Boolean?,
    /** Prose, not a minimum age: the one activity that states a limit states two at once. */
    val suitability: String?,
    val supervised: Boolean?,
    val menu: List<MenuGroup>,
    val links: List<Link>,
    /** Null when this fiche has no single heart, which is everything that is not a Stand. */
    val wishlisted: Boolean?,
)
