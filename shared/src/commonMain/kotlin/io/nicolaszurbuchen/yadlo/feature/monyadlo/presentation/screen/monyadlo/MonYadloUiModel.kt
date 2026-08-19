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
 * One day's block, as the three lines of its rail.
 *
 * [name] is what someone thinks in ("samedi"); [dayNumber] is what they look for when they already
 * know which day it is and are scrolling to find it, which is why it is the largest thing on the
 * rail; [monthName] is what stops a bare 11 from being ambiguous the year the festival moves.
 */
data class MonYadloDayUiModel(
    val id: String,
    val name: String,
    val dayNumber: String,
    val monthName: UiText,
    val rows: List<MonYadloRowUiModel>,
)

/**
 * One saved Slot.
 *
 * No bar, unlike the Programme row it mirrors: the bar places a Slot against the whole day's span,
 * which is a question about a day you are choosing from rather than one you have already chosen.
 *
 * The price is here, and was not. The argument against it was that the decision had been made when
 * the heart was tapped — true of the decision, and not of the coins in your pocket. A Plan read on
 * the site is a list of what you are about to do, and what two of them cost is part of that.
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
    val priceText: UiText?,
    val stateLabel: UiText?,
    val state: SlotLiveStateUiModel,
)
