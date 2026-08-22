package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryTagUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * One fiche, whichever of the three kinds of Happening it is.
 *
 * The template is the same for an Artist, an Activity and a Stand, and the difference between them
 * is which of these lists is empty — DECISIONS.md § One fiche template for everything. Nothing here
 * says which kind it is, because no part of the screen needs to know.
 *
 * [isMissing] is not an error state. It is the Happening no longer being in the content, which is
 * reachable by restoring the app onto a fiche a refresh has since dropped.
 *
 * [wishlisted] is the one field whose *nullness* is a layout instruction: non-null draws the heart
 * in the bar, null leaves the hearts on the date rows. That is the Stand / not-a-Stand distinction
 * arriving as the only question the screen actually has about it.
 */
data class HappeningUiModel(
    val isLoading: Boolean,
    val isMissing: Boolean,
    val title: String,
    val categoryId: String,
    val categoryLabel: String,
    /**
     * The photograph behind the title, or null for a Happening the content has none for — two of
     * the 38, both stands, and the number only ever goes down.
     *
     * Unlike [wishlisted]'s, this null is not a layout instruction. The head of the fiche is a
     * photograph either way; a null one means the bundled picture of the site goes behind the title
     * instead — DECISIONS.md § The fiche has one ground.
     */
    val imageUrl: String?,
    val description: String?,
    val tags: List<String>,
    /** What a Stand can feed you, derived from its menu. Empty for everything that is not one. */
    val dietary: List<YadloDietaryTagUiModel>,
    val slots: List<HappeningSlotUiModel>,
    val price: HappeningPriceUiModel?,
    val booking: HappeningBookingUiModel?,
    val facts: List<UiText>,
    val menu: List<HappeningMenuGroupUiModel>,
    /**
     * Where else this Happening exists — its own site, and whichever of the nine platforms it
     * publishes on. The footer's own model, because a fiche's *Liens* section and the foot of
     * Accueil are the same row of marks doing the same job.
     */
    val links: List<SocialLinkUiModel>,
    val wishlisted: Boolean?,
    /**
     * The message the share sheet sends, assembled here so the screen has nothing to build and a
     * test can read the whole thing.
     *
     * [UiText] rather than a String because the opening sentence names the kind — *viens voir ce
     * concert*, *je te partage ce stand* — and that is copy, which lives in a resource. Everything
     * under it is raw: names and days come from the content, times and dates are numeric, so the
     * whole body travels as this resource’s one argument and stays testable without a composition.
     *
     * Null while the fiche is loading or missing, which is the one state the action hides in —
     * there is no title to put in a message yet.
     */
    val shareText: UiText?,
)

/**
 * A date row. Carries the same live state as a Programme row, so a Slot that read `en cours` on the
 * list still reads `en cours` on the screen the list opened.
 *
 * This is the row the heart is attached to — DECISIONS.md § The heart is attached to what you are
 * saving — and the whole row is the target, including the ones that are already over. Saving the
 * Friday of a three-day activity after it has run is not a mistake worth preventing; it is how
 * someone marks what they went to.
 */
data class HappeningSlotUiModel(
    val id: String,
    val dayName: String,
    /**
     * The day and month the row writes after [dayName] — *samedi 10 juillet*. Split in two because
     * only one of them can be formatted: the number is a number in any language and the month is a
     * word that has to be looked up, which is why [monthName] is a [UiText] and this is not.
     */
    val dayNumber: String,
    val monthName: UiText,
    val timeText: String,
    val stateLabel: UiText?,
    val state: SlotLiveStateUiModel,
    val planned: Boolean,
)

/**
 * [deposit] is written on its own line and never summed into a tier: the Silent Party is CHF 25 with
 * a CHF 50 headset deposit, and CHF 75 is wrong in the direction that stops someone coming.
 */
data class HappeningPriceUiModel(
    val tiers: List<HappeningPriceTierUiModel>,
    val deposit: UiText?,
    val depositNote: String?,
)

/**
 * [label] is null when one price covers everyone, which is most of them. [amount] is a [UiText]
 * because a free activity is a tier too, and the word it carries is copy rather than a number.
 */
data class HappeningPriceTierUiModel(
    val label: String?,
    val amount: UiText,
)

/** [url] is null when the content says a booking is required but does not say where. */
data class HappeningBookingUiModel(
    val label: UiText,
    val url: String?,
)

/**
 * One group of a stand's carte. [name] is the section header it is drawn under, at the same level
 * as *Quand* and *Tarifs* — a group is a section of the fiche rather than a sub-heading under one.
 *
 * **No `source`.** The content still records where each carte came from, on the group and on every
 * item; the fiche stopped writing it out. Five groups ending in five copies of the same forty-word
 * disclaimer taught a reader to skip small grey text, which is the opposite of what it was for.
 */
data class HappeningMenuGroupUiModel(
    val id: String,
    val name: String,
    val description: String?,
    val items: List<HappeningMenuItemUiModel>,
)

/**
 * Two independent rows — the name with its marks and its price, then the ingredients — so nothing
 * shares a line with the price and nothing can overflow into it.
 *
 * [dietary] is drawn as glyphs alone here. The words are written once at the top of the fiche, over
 * the whole stand, which is what makes the glyph on a dish readable rather than a symbol to guess
 * at — see [io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryMarks].
 */
data class HappeningMenuItemUiModel(
    val name: String,
    val priceText: String?,
    val description: String?,
    val dietary: List<YadloDietaryTagUiModel>,
)
