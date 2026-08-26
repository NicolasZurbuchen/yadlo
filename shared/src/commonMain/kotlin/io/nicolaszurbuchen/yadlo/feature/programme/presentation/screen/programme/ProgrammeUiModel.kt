package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * The tab, showing whichever thing its one selector row is pointing at.
 *
 * **Exactly one of [sections] and [catalogue] is ever populated.** They are two fields rather than
 * one sealed body because they are genuinely different shapes over genuinely different units — a
 * day of Slots against a Happening — and a screen that had to unwrap a sealed type to find out
 * whether it was drawing a list or a grid would be spelling [scopes] a second way.
 *
 * [scale] is the axis written once in the chrome, and it is non-null only when the list is one
 * day: across several, each day has its own span and the scale belongs to the sticky header that
 * comes with it. [categories] survives every scope, because "montre-moi ce qui est sur l'eau" is
 * the same question whichever list is under it.
 *
 * [scopes] is empty when there is nothing to point at — before the first bundle, and when the
 * edition has published dates but no programme. Offering a row of controls that cannot change the
 * screen is the same mistake as offering three day chips with nothing under any of them.
 *
 * [emptyMessage] is non-null exactly when the showing half is empty, and it says which kind of
 * empty — a filter that matched nothing reads differently from a programme not yet published.
 */
data class ProgrammeUiModel(
    val isLoading: Boolean,
    val scopes: List<ScopeChipUiModel>,
    val categories: List<CategoryChipUiModel>,
    val scale: SlotScaleUiModel?,
    val sections: List<DaySectionUiModel>,
    val catalogue: List<CatalogueCardUiModel>,
    val emptyMessage: UiText?,
)

/**
 * [label] is a [UiText] rather than a String because the row mixes two provenances: *Découvrir* and
 * *Tous* are the app's words and are translated with it, while a day's name comes out of the
 * content so that a day the association calls something else keeps its name.
 */
data class ScopeChipUiModel(
    val id: String,
    val label: UiText,
    val isSelected: Boolean,
)

data class CategoryChipUiModel(
    val id: String,
    val name: String,
    val isSelected: Boolean,
)

/**
 * One day of the list, and the unit the timetable is built out of whether it is showing one day or
 * three.
 *
 * [header] is null when the list is a single day, because the chip row directly above already says
 * which day that is and a header under it would say it twice. Across several days it is what makes
 * the scroll readable, and it carries [DaySectionHeaderUiModel.scale] with it — see there for why
 * the axis travels with the header rather than staying in the chrome.
 *
 * **A day with nothing on it after the filter is absent, not empty.** Three headers with one row
 * under them says less about a weekend than one header does, and an empty Saturday reads as a
 * screen that failed rather than as a Saturday nothing matched on. Same rule Mon Yadlo's timeline
 * applies to a day with nothing saved.
 */
data class DaySectionUiModel(
    val id: String,
    val header: DaySectionHeaderUiModel?,
    val rows: List<SlotRowUiModel>,
)

/**
 * The day's name and the span its rows' bars are drawn against, pinned while those rows are on
 * screen.
 *
 * **The axis travels with the header because a span is a fact about one day.** Friday runs
 * 16:00–02:00 and Sunday 12:00–22:00, so a single scale in the chrome could only be right about
 * one of them — and a scale that is wrong about the bars under it is worse than no scale, because
 * it does not fail to answer the question, it answers it wrongly. Sticking the header is what makes
 * that work: the reading on screen always belongs to the rows on screen.
 */
data class DaySectionHeaderUiModel(
    val name: String,
    val scale: SlotScaleUiModel,
)

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
