package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * Layout B2: one chronological list for the selected day, no calendar column and no "now" line.
 *
 * [emptyMessage] is non-null exactly when [rows] is empty, and it says which kind of empty — a
 * filter that matched nothing reads differently from a programme that has not been published.
 */
data class ProgrammeUiModel(
    val isLoading: Boolean,
    val days: List<DayChipUiModel>,
    val categories: List<CategoryChipUiModel>,
    val scale: SlotScaleUiModel?,
    val rows: List<SlotRowUiModel>,
    val emptyMessage: UiText?,
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
