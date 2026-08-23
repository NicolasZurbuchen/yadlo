package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.loudestState
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.slotLiveStateAt
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.ui.formatMoney
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.price_from
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.time.Duration

/**
 * Whichever of the tab's two views is showing.
 *
 * **The Catalogue leaves before the day is chosen**, because everything below that point — the day
 * chips, the axis the bars are measured on, the live states — is about a timetable it does not
 * have. What the two views share is the Category chips and the empty message, and both are built
 * above the branch so neither view can drift from the other on them.
 *
 * The Programme half: one day's Happenings, in order, each carrying every hour it runs that day,
 * its own state against the clock and its own place on the day's span.
 *
 * Everything is built inside this single function: a UiMapper file is required to hold nothing but
 * the State-to-UiModel extension, so a helper here would have to be local, which Konsist reads as
 * an extra function in the file. The pieces that would otherwise want to be helpers live elsewhere
 * on purpose — `slotLiveStateAt` and `loudestState` beside the states they return, `formatMoney` in
 * `infra/ui` where the fiche finds it too.
 */
fun ProgrammeState.toUiModel(): ProgrammeUiModel {
    val loaded =
        content ?: return ProgrammeUiModel(
            isLoading = true,
            view = null,
            days = emptyList(),
            categories = emptyList(),
            scale = null,
            rows = emptyList(),
            catalogue = emptyList(),
            emptyMessage = null,
        )

    // No Slots at all is an edition published with dates and nothing under them, which happens
    // every spring. The day chips go with the list: offering three days to switch between when
    // none of them has anything reads as a screen that failed to load.
    if (loaded.slots.isEmpty()) {
        // The toggle goes with the chips: switching to a Catalogue of things nobody has published
        // hours for would answer a different question than the one this screen just failed to.
        return ProgrammeUiModel(
            isLoading = false,
            view = null,
            days = emptyList(),
            categories = emptyList(),
            scale = null,
            rows = emptyList(),
            catalogue = emptyList(),
            emptyMessage = UiText.Resource(Res.string.programme_empty_unpublished),
        )
    }

    // Null only in the frame between the store being built and the first bundle landing, which the
    // loading branch above has already returned for. The timetable is the fallback because it is
    // the tab's name.
    val view = selectedView ?: ProgrammeViewUiModel.PROGRAMME

    val categories =
        loaded.categories.map {
            CategoryChipUiModel(id = it.id, name = it.name, isSelected = it.id in selectedCategoryIds)
        }

    if (view == ProgrammeViewUiModel.CATALOGUE) {
        val entries =
            loaded.catalogue
                // The same reading of an empty selection as the list below: *Tout*, not a filter
                // that excludes everything.
                .filter { selectedCategoryIds.isEmpty() || it.categoryId in selectedCategoryIds }
                .map { entry ->
                    CatalogueCardUiModel(
                        id = entry.id,
                        name = entry.name,
                        categoryId = entry.categoryId,
                        categoryName = entry.categoryName,
                        description = entry.description,
                        imageUrl = entry.imageUrl,
                        genres = entry.genres,
                    )
                }

        return ProgrammeUiModel(
            isLoading = false,
            view = view,
            // Both empty on purpose: a Catalogue entry has no day and no hours, so a day chip could
            // only filter it by something it does not carry, and an axis would measure nothing.
            days = emptyList(),
            categories = categories,
            scale = null,
            rows = emptyList(),
            catalogue = entries,
            emptyMessage = if (entries.isEmpty()) UiText.Resource(Res.string.programme_empty_filter) else null,
        )
    }

    val days = loaded.days.map { DayChipUiModel(id = it.id, name = it.name, isSelected = it.id == selectedDayId) }

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
            // One row per Happening, with its hours on it — DECISIONS.md § A row is a Happening on
            // a day. `groupBy` keeps first-seen order and the Slots arrive sorted, so the rows come
            // out in the order their first hour starts and each row's hours are chronological.
            .groupBy { it.happeningId }
            .map { (happeningId, slots) ->
                val first = slots.first()

                val segments =
                    slots.map { slot ->
                        SlotSegmentUiModel(
                            id = slot.id,
                            timeText =
                                "${slot.start.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                                    slot.end.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                            state = slotLiveStateAt(now = now, start = slot.start, end = slot.end),
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

                val state = segments.loudestState()

                SlotRowUiModel(
                    // The day is in it because the same Happening runs on all three of them, and a
                    // list key that repeats across days is a list that reuses a row's scroll state
                    // for a different day's copy of it.
                    id = "${first.dayId}/$happeningId",
                    happeningId = happeningId,
                    name = first.name,
                    categoryId = first.categoryId,
                    categoryName = first.categoryName,
                    priceText =
                        first.price?.let { price ->
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
                                        Res.string.price_from,
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
                    slots = segments,
                )
            }

    return ProgrammeUiModel(
        isLoading = false,
        view = view,
        days = days,
        categories = categories,
        scale =
            if (hasAxis && axisStart != null && axisEnd != null && rows.isNotEmpty()) {
                SlotScaleUiModel(
                    startText = axisStart.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    middleText = (axisStart + axisSpan / 2).formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    endText = axisEnd.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                )
            } else {
                null
            },
        rows = rows,
        catalogue = emptyList(),
        emptyMessage = if (rows.isEmpty()) UiText.Resource(Res.string.programme_empty_filter) else null,
    )
}
