package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.slotLiveStateAt
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.ui.formatMoney
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.programme_price_from
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.time.Duration

/**
 * One day's Slots, in order, each carrying its own state against the clock and its own place on the
 * day's span.
 *
 * Everything is built inside this single function: a UiMapper file is required to hold nothing but
 * the State-to-UiModel extension, so a helper here would have to be local, which Konsist reads as
 * an extra function in the file. The two pieces that would otherwise want to be helpers live
 * elsewhere on purpose — `slotLiveStateAt` beside the state it returns, `formatMoney` in `infra/ui`
 * where the fiche will find it too.
 */
fun ProgrammeState.toUiModel(): ProgrammeUiModel {
    val loaded =
        content ?: return ProgrammeUiModel(
            isLoading = true,
            days = emptyList(),
            categories = emptyList(),
            scale = null,
            rows = emptyList(),
            emptyMessage = null,
        )

    // No Slots at all is an edition published with dates and nothing under them, which happens
    // every spring. The day chips go with the list: offering three days to switch between when
    // none of them has anything reads as a screen that failed to load.
    if (loaded.slots.isEmpty()) {
        return ProgrammeUiModel(
            isLoading = false,
            days = emptyList(),
            categories = emptyList(),
            scale = null,
            rows = emptyList(),
            emptyMessage = UiText.Resource(Res.string.programme_empty_unpublished),
        )
    }

    val days = loaded.days.map { DayChipUiModel(id = it.id, name = it.name, isSelected = it.id == selectedDayId) }

    val categories =
        loaded.categories.map {
            CategoryChipUiModel(id = it.id, name = it.name, isSelected = it.id in selectedCategoryIds)
        }

    val daySlots = loaded.slots.filter { it.dayId == selectedDayId }
    val selectedDay = loaded.days.firstOrNull { it.id == selectedDayId }

    // The axis is the day's opening hours widened to cover anything programmed outside them: the
    // beach at Préverenges is public, so the morning yoga runs from 10:00 on a day the site opens
    // at 12:00 and still has to sit on the bar rather than off the left edge of it.
    //
    // Measured across every Slot of the day, never the filtered ones — an axis that rescaled when
    // you tapped a chip would make two rows impossible to compare across a filter change.
    val axisStart = (listOfNotNull(selectedDay?.start) + daySlots.map { it.start }).minOrNull()
    val axisEnd = (listOfNotNull(selectedDay?.end) + daySlots.map { it.end }).maxOrNull()
    val axisSpan = if (axisStart != null && axisEnd != null) axisEnd - axisStart else Duration.ZERO
    val hasAxis = axisStart != null && axisEnd != null && axisSpan.isPositive()

    val rows =
        daySlots
            // An empty selection is *Tout*, not a filter that excludes everything.
            .filter { selectedCategoryIds.isEmpty() || it.categoryId in selectedCategoryIds }
            .map { slot ->
                val state = slotLiveStateAt(now = now, start = slot.start, end = slot.end)

                SlotRowUiModel(
                    id = slot.id,
                    happeningId = slot.happeningId,
                    name = slot.name,
                    categoryId = slot.categoryId,
                    categoryName = slot.categoryName,
                    timeText =
                        "${slot.start.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                            slot.end.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    priceText =
                        slot.price?.let { price ->
                            // `tiers` is empty exactly when `free` is true, and the content
                            // validator holds that — so a missing cheapest tier means free too.
                            val cheapest = price.tiers.minByOrNull { it.amount.amount }
                            when {
                                price.free || cheapest == null -> {
                                    UiText.Resource(Res.string.price_free)
                                }

                                // "dès CHF 15" rather than the adult price: the Silent Party is
                                // CHF 25 for adults and CHF 15 under 16, and a row that shows only
                                // the higher one prices a family out of something they can afford.
                                price.tiers.size > 1 -> {
                                    UiText.Resource(
                                        Res.string.programme_price_from,
                                        listOf(formatMoney(cheapest.amount.amount, cheapest.amount.currency)),
                                    )
                                }

                                else -> {
                                    UiText.Raw(formatMoney(cheapest.amount.amount, cheapest.amount.currency))
                                }
                            }
                        },
                    stateLabel =
                        when (state) {
                            SlotLiveStateUiModel.Upcoming -> {
                                null
                            }

                            is SlotLiveStateUiModel.StartingSoon -> {
                                // Always minutes, because the window is an hour: an hours branch
                                // could only ever fire on the single instant the window opens.
                                // Never "dans 0 min" either — under a minute out it still has not
                                // started, and one is the smallest true thing to say.
                                UiText.Resource(
                                    Res.string.slot_state_starts_in_minutes,
                                    listOf(state.startsIn.inWholeMinutes.coerceAtLeast(1).toString()),
                                )
                            }

                            is SlotLiveStateUiModel.Running -> {
                                UiText.Resource(Res.string.slot_state_running)
                            }

                            is SlotLiveStateUiModel.Ending -> {
                                UiText.Resource(
                                    Res.string.slot_state_ending,
                                    listOf(state.endsIn.inWholeMinutes.coerceAtLeast(1).toString()),
                                )
                            }

                            SlotLiveStateUiModel.Over -> {
                                UiText.Resource(Res.string.slot_state_over)
                            }
                        },
                    state = state,
                    barStart =
                        if (hasAxis && axisStart != null) {
                            ((slot.start - axisStart) / axisSpan).toFloat().coerceIn(0f, 1f)
                        } else {
                            0f
                        },
                    barEnd =
                        if (hasAxis && axisStart != null) {
                            ((slot.end - axisStart) / axisSpan).toFloat().coerceIn(0f, 1f)
                        } else {
                            1f
                        },
                )
            }

    return ProgrammeUiModel(
        isLoading = false,
        days = days,
        categories = categories,
        scale =
            if (hasAxis && axisStart != null && axisEnd != null && rows.isNotEmpty()) {
                ProgrammeScaleUiModel(
                    startText = axisStart.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    middleText = (axisStart + axisSpan / 2).formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    endText = axisEnd.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                )
            } else {
                null
            },
        rows = rows,
        emptyMessage = if (rows.isEmpty()) UiText.Resource(Res.string.programme_empty_filter) else null,
    )
}
