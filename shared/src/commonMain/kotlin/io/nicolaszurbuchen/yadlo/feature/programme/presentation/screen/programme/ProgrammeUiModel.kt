package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * The tab in whichever of its two views is showing: layout B2 — one chronological list for the
 * selected day, no calendar column and no "now" line — or the Catalogue's grid of everything the
 * festival offers, with no hour on it.
 *
 * **Exactly one of [rows] and [catalogue] is ever populated**, and [view] says which. They are two
 * fields rather than one sealed body because they are genuinely different shapes over genuinely
 * different units — a Slot's day against a Happening — and a screen that had to unwrap a sealed
 * type to find out whether it was drawing a list or a grid would be spelling [view] a second way.
 *
 * [days] and [scale] are empty and null in the Catalogue, which is what takes the day chips and the
 * hour axis off the chrome: a grid with no time on it has no day to be on and nothing to measure
 * against. [categories] survives both, because "montre-moi ce qui est sur l'eau" is the same
 * question either way — and it is most of what makes the Catalogue browsable at all.
 *
 * [view] is null when there is nothing to switch between: before the first bundle, and when the
 * edition has published dates but no programme. Offering a control that cannot change the screen is
 * the same mistake as offering three day chips with nothing under any of them.
 *
 * [emptyMessage] is non-null exactly when the showing half is empty, and it says which kind of
 * empty — a filter that matched nothing reads differently from a programme not yet published.
 */
data class ProgrammeUiModel(
    val isLoading: Boolean,
    val view: ProgrammeViewUiModel?,
    val days: List<DayChipUiModel>,
    val categories: List<CategoryChipUiModel>,
    val scale: SlotScaleUiModel?,
    val rows: List<SlotRowUiModel>,
    val catalogue: List<CatalogueCardUiModel>,
    val emptyMessage: UiText?,
)

/**
 * The two questions the tab can answer: *when is it on*, and *what is there*.
 *
 * Which one it opens on follows the Phase — see ProgrammeStoreFactory — and after that it follows
 * the visitor.
 */
enum class ProgrammeViewUiModel {
    PROGRAMME,
    CATALOGUE,
}

/**
 * One Happening in the Catalogue, drawn as a photograph rather than as a row.
 *
 * **The same three bands as a Stand's card, for the same reasons** (DECISIONS.md § A Stand is a
 * photograph): the picture, then what it is and what it is called, then — behind a rule, skippable
 * by anyone who does not need it — the attributes. It is deliberately the same shape, because a
 * visitor moving between *Nourriture & boissons* and this grid is doing one thing, which is looking
 * at what the festival has.
 *
 * **[categoryId] and [categoryName] both, and the Category is written out.** This is the one browse
 * list in the app that mixes Categories — the stands lists are each a single one — so the card has
 * to say which. Colour never carries it alone: in July sun, on a phone, the word is what survives.
 *
 * [description] is the content's own, clamped by the card rather than shortened here. The edition's
 * run from 41 to 595 characters and there is no authored short form; a truncation that happened in
 * the mapper would be a decision about layout taken where the layout is not known.
 *
 * [genres] is absent rather than empty for most Activities, which is why the band it sits in is
 * drawn only when there is something in it.
 */
data class CatalogueCardUiModel(
    val id: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val description: String?,
    val imageUrl: String?,
    val genres: List<String>,
)

data class DayChipUiModel(
    val id: String,
    val name: String,
    val isSelected: Boolean,
)

data class CategoryChipUiModel(
    val id: String,
    val name: String,
    val isSelected: Boolean,
)

/**
 * One row of the day — **one Happening, with every hour it runs that day on it.**
 *
 * It was one row per Slot, and SUP Yoga is what broke that: it runs 14:00, 16:00 and 18:00 on the
 * Saturday, which the list drew as three entries with the same name, the same Category, the same
 * price and the same photograph, scattered among everything programmed between them. Three lines
 * saying one thing, and the thing they were saying wrong — that these are three activities rather
 * than three chances at one. The fiche was already right about this: it has always been one
 * Happening with a list of dates, and there is no screen for a single Slot. DECISIONS.md § A row is
 * a Happening on a day.
 *
 * [slots] carries them in order, and the row writes them on one line — *14:00 – 15:00 · 16:00 –
 * 17:00 · 18:00 – 19:00* — with each one dimmed on its own once it is past. A row whose 14:00 has
 * gone is not a row that has gone.
 *
 * [state] is the loudest of [slots], never the first: something running now takes the row, and
 * `terminé` only appears when every hour of it is finished. The alternative — the earliest Slot
 * speaking for the row — puts `terminé` on an activity that starts again in twenty minutes, which
 * is the one thing about a merged row a reader could call a lie.
 *
 * [categoryId] rather than a colour: the colour of a Category is a design decision the theme owns,
 * and it is never the only thing carrying the meaning — [categoryName] is written out on every row.
 *
 * [stateLabel] is null when there is nothing to say, which is most of the year and most of a day.
 * [state] is what the row is drawn from — the pill's treatment and the dimming — and it is separate
 * from the label because the label is a translated string and the treatment is not.
 */
data class SlotRowUiModel(
    /** The day and the Happening, since the row is no longer one Slot and cannot borrow its id. */
    val id: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val priceText: UiText?,
    val stateLabel: UiText?,
    val state: SlotLiveStateUiModel,
    val slots: List<SlotSegmentUiModel>,
)
