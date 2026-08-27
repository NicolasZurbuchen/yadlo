package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * The rail variant — DECISIONS.md § Mon Yadlo layout: a date pinned to the left while its items
 * scroll past, the time written once as a range, the same row vocabulary as Programme.
 *
 * [scale] is the span every row's bar is drawn against, written once in the chrome rather than once
 * per day — DECISIONS.md § Mon Yadlo's bars share one axis across the days. Null exactly when there
 * is nothing planned, since an axis over no days describes nothing.
 *
 * [emptyMessage] is non-null exactly when [days] is empty and the screen has loaded. It points at
 * the Programme rather than offering an add-flow: Mon Yadlo recalls, it never browses.
 */
data class MonYadloUiModel(
    val isLoading: Boolean,
    val scale: SlotScaleUiModel?,
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
 * **It carries a bar now, and it did not.** The argument against was that a bar places a Slot
 * against the whole day's span, which is a question about a day you are choosing from rather than
 * one you have already chosen. That was half right: you are not choosing any more, but on the Sunday
 * afternoon "how much of this is left, and what have I got after it" is exactly the question a Plan
 * is open for. DECISIONS.md § Mon Yadlo's bars share one axis across the days.
 *
 * The price is here, and was not. The argument against it was that the decision had been made when
 * the heart was tapped — true of the decision, and not of the coins in your pocket. A Plan read on
 * the site is a list of what you are about to do, and what two of them cost is part of that.
 *
 * No heart: the row opens the fiche, which is the one screen that owns this Slot's heart. Never two
 * hearts for the same thing — DECISIONS.md § The heart is attached to what you are saving.
 *
 * **[slot] holds the time, the state and the place on the axis together**, so the line under the
 * name and the mark on the bar cannot disagree about which Slot they are describing. Exactly one,
 * unlike a Programme row: Mon Yadlo does not merge a Happening's hours, because a Plan is what you
 * are doing in order and two hours of yoga you kept are two appointments.
 */
data class MonYadloRowUiModel(
    val id: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val priceText: UiText?,
    val stateLabel: UiText?,
    val slot: SlotSegmentUiModel,
)
