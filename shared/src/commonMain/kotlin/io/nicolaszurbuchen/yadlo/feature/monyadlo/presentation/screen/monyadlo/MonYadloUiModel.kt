package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * The rail variant — DECISIONS.md § Mon Yadlo layout: a date pinned to the left while its items
 * scroll past, the time written once as a range, the same row vocabulary as Programme.
 *
 * [emptyMessage] is non-null exactly when [days] is empty and the screen has loaded. It points at
 * the Programme rather than offering an add-flow: Mon Yadlo recalls, it never browses.
 */
data class MonYadloUiModel(
    val isLoading: Boolean,
    val days: List<MonYadloDayUiModel>,
    val wishlistCount: Int,
    val emptyMessage: UiText?,
)

/**
 * One day's block. [name] and [dateText] are both written on the rail — the name is what someone
 * thinks in ("samedi"), the date is what a poster and a bus timetable are written in.
 */
data class MonYadloDayUiModel(
    val id: String,
    val name: String,
    val dateText: String,
    val rows: List<MonYadloRowUiModel>,
)

/**
 * One saved Slot.
 *
 * No bar and no price, unlike the Programme row it mirrors. The bar places a Slot against the whole
 * day's span, which is a question about a day you are choosing from rather than one you have already
 * chosen; the price is a decision that was made when the heart was tapped.
 *
 * No heart either: the row opens the fiche, which is the one screen that owns this Slot's heart.
 * Never two hearts for the same thing — DECISIONS.md § The heart is attached to what you are saving.
 */
data class MonYadloRowUiModel(
    val id: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val timeText: String,
    val stateLabel: UiText?,
    val state: SlotLiveStateUiModel,
)
