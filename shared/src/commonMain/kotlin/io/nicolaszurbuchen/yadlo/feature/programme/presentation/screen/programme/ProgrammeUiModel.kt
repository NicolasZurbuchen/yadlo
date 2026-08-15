package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

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
    val scale: ProgrammeScaleUiModel?,
    val rows: List<SlotRowUiModel>,
    val emptyMessage: UiText?,
)

/**
 * The three times written across the head of the list — the span every row's bar is drawn against.
 *
 * Written once at the top rather than on each row: it is one axis shared by the whole day, and
 * repeating it per row is the right-hand time column layout B2 exists to avoid.
 */
data class ProgrammeScaleUiModel(
    val startText: String,
    val middleText: String,
    val endText: String,
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
 * One row of the day.
 *
 * [categoryId] rather than a colour: the colour of a Category is a design decision the theme owns,
 * and it is never the only thing carrying the meaning — [categoryName] is written out beside the
 * time on every row.
 *
 * [barStart] and [barEnd] place the Slot on the day's span as fractions of it, and they are on
 * **every** row, finished ones included. The bar is where the day is legible at a glance — what
 * overlaps what, how much of the afternoon a seven-hour activity covers, how far in you are — and
 * a row that loses it the moment it ends takes the shape of the day with it.
 *
 * [stateLabel] is null when there is nothing to say, which is most of the year and most of a day.
 * [state] is what the row is drawn from — the pill's treatment, whether the bar is raised and how
 * far its progress has run, the dimming — and it is separate from the label because the label is a
 * translated string and the treatment is not.
 */
data class SlotRowUiModel(
    val id: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val timeText: String,
    val priceText: UiText?,
    val stateLabel: UiText?,
    val state: SlotLiveStateUiModel,
    val barStart: Float,
    val barEnd: Float,
)
