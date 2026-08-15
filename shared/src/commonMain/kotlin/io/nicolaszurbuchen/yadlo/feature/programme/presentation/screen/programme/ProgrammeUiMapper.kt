package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.ui.formatMoney
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.programme_price_free
import yadlo.shared.generated.resources.programme_price_from
import yadlo.shared.generated.resources.programme_state_ending
import yadlo.shared.generated.resources.programme_state_over
import yadlo.shared.generated.resources.programme_state_running
import yadlo.shared.generated.resources.programme_state_starts_in_hours
import yadlo.shared.generated.resources.programme_state_starts_in_minutes
import kotlin.time.Duration.Companion.hours

/**
 * One day's Slots, in order, each carrying its own state against the clock.
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
            rows = emptyList(),
            emptyMessage = UiText.Resource(Res.string.programme_empty_unpublished),
        )
    }

    val days = loaded.days.map { DayChipUiModel(id = it.id, name = it.name, isSelected = it.id == selectedDayId) }

    val categories =
        loaded.categories.map {
            CategoryChipUiModel(id = it.id, name = it.name, isSelected = it.id in selectedCategoryIds)
        }

    val rows =
        loaded.slots
            .filter { it.dayId == selectedDayId }
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
                                    UiText.Resource(Res.string.programme_price_free)
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
                                if (state.startsIn >= 1.hours) {
                                    // Floored: at 3h50 the answer someone wants is "not for a
                                    // while", and rounding it up to four crosses back out of the
                                    // window the countdown is only shown inside.
                                    UiText.Resource(
                                        Res.string.programme_state_starts_in_hours,
                                        listOf(state.startsIn.inWholeHours.toString()),
                                    )
                                } else {
                                    // Never "dans 0 min": under a minute out it still has not
                                    // started, and one is the smallest true thing to say.
                                    UiText.Resource(
                                        Res.string.programme_state_starts_in_minutes,
                                        listOf(state.startsIn.inWholeMinutes.coerceAtLeast(1).toString()),
                                    )
                                }
                            }

                            is SlotLiveStateUiModel.Running -> {
                                UiText.Resource(Res.string.programme_state_running)
                            }

                            is SlotLiveStateUiModel.Ending -> {
                                UiText.Resource(
                                    Res.string.programme_state_ending,
                                    listOf(state.endsIn.inWholeMinutes.coerceAtLeast(1).toString()),
                                )
                            }

                            SlotLiveStateUiModel.Over -> {
                                UiText.Resource(Res.string.programme_state_over)
                            }
                        },
                    state = state,
                )
            }

    return ProgrammeUiModel(
        isLoading = false,
        days = days,
        categories = categories,
        rows = rows,
        emptyMessage = if (rows.isEmpty()) UiText.Resource(Res.string.programme_empty_filter) else null,
    )
}
