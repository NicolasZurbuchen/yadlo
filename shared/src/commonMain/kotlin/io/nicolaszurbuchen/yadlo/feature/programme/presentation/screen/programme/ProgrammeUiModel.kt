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
 * One row of the day.
 *
 * [categoryId] rather than a colour: the colour of a Category is a design decision the theme owns,
 * and it is never the only thing carrying the meaning — [categoryName] is written out beside the
 * time on every row.
 *
 * [stateLabel] is null when there is nothing to say, which is most of the year and most of a day.
 * [state] is what the row is drawn from — the pill's treatment, the raised bar, the dimming — and
 * the two are separate because the label is a translated string and the treatment is not.
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
)
