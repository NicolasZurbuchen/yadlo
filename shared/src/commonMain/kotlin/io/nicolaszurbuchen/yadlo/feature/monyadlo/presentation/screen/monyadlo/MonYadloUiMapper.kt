package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.slotLiveStateAt
import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.format.formatAsDayOfMonth
import io.nicolaszurbuchen.yadlo.infra.format.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.format.formatMoney
import io.nicolaszurbuchen.yadlo.infra.format.monthName
import io.nicolaszurbuchen.yadlo.infra.format.startOfDayIn
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_empty
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.price_from
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.time.Duration

/**
 * The Plan, day by day, each row measured against the same clock the Programme reads and placed on
 * an axis shared by every day on the screen.
 *
 * **One axis, not one per day.** Mon Yadlo shows all three days at once, so a scale written once in
 * the chrome — which is where it has to be, since the rail takes the left of every row — can only be
 * honest if every bar under it means the same thing. Each Slot is therefore measured as a *clock*
 * reading, counted from its own day's midnight, and the axis is the union of the three days'
 * windows: 12:00 to 03:00 for the 2026 edition. Friday opens at 16:00 and its bars start a quarter
 * of the way in, which is not wasted space — it is the true statement that Friday starts later.
 *
 * That is deliberately not the Programme's axis, which is one day wide and starts at that day's own
 * first hour. The two screens answer different questions: the Programme compares Slots within a day
 * you are choosing from, and a Plan compares the three days you have.
 *
 * Everything is inline for the reason the other two UiMappers are: the file may hold nothing but its
 * own State-to-UiModel extension, so the live-state words are written out here as well. That
 * repetition is deliberate and recorded — the alternative is a UiText-returning helper in `infra/ui`
 * pretending a copy decision is a formatting one.
 */
fun MonYadloState.toUiModel(): MonYadloUiModel {
    val loaded =
        content ?: return MonYadloUiModel(
            isLoading = true,
            scale = null,
            days = emptyList(),
            wishlistCount = 0,
            emptyMessage = null,
        )

    // Each day's window as two offsets from its own midnight, so three days that open at different
    // clock times can be laid over one another. Subtracting the instants directly would measure two
    // days of real time between the Friday and the Sunday.
    val midnights = loaded.days.associate { it.id to it.start.startOfDayIn(FESTIVAL_TIME_ZONE) }
    val axisFrom = loaded.days.minOfOrNull { it.windowStart - midnights.getValue(it.id) }
    val axisTo = loaded.days.maxOfOrNull { it.windowEnd - midnights.getValue(it.id) }
    val axisSpan = if (axisFrom != null && axisTo != null) axisTo - axisFrom else Duration.ZERO
    val hasAxis = axisFrom != null && axisSpan.isPositive()

    // Any day's midnight would print the same clock readings; the first one is simply the one that
    // exists whenever the axis does.
    val reference = loaded.days.firstOrNull()?.let { midnights.getValue(it.id) }

    return MonYadloUiModel(
        isLoading = false,
        scale =
            if (hasAxis && axisFrom != null && reference != null) {
                SlotScaleUiModel(
                    startText = (reference + axisFrom).formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    middleText = (reference + axisFrom + axisSpan / 2).formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    endText = (reference + axisFrom + axisSpan).formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                )
            } else {
                null
            },
        days =
            loaded.days.map { day ->
                val midnight = midnights.getValue(day.id)

                MonYadloDayUiModel(
                    id = day.id,
                    name = day.name,
                    dayNumber = day.start.formatAsDayOfMonth(FESTIVAL_TIME_ZONE),
                    monthName = day.start.monthName(FESTIVAL_TIME_ZONE),
                    rows =
                        day.slots.map { slot ->
                            val state = slotLiveStateAt(now = now, start = slot.start, end = slot.end)

                            MonYadloRowUiModel(
                                id = slot.id,
                                happeningId = slot.happeningId,
                                name = slot.name,
                                categoryId = slot.categoryId,
                                categoryName = slot.categoryName,
                                // Written exactly as the Programme writes it, down to the "dès" on
                                // a multi-tier price: the same Slot must not cost two things
                                // depending on which screen it is read from.
                                priceText =
                                    slot.price?.let { price ->
                                        val cheapest = price.tiers.minByOrNull { it.amount.amount }
                                        when {
                                            price.free || cheapest == null -> {
                                                UiText.Resource(Res.string.price_free)
                                            }

                                            price.tiers.size > 1 -> {
                                                UiText.Resource(
                                                    Res.string.price_from,
                                                    listOf(
                                                        formatMoney(
                                                            cheapest.amount.amount,
                                                            cheapest.amount.currency,
                                                        ),
                                                    ),
                                                )
                                            }

                                            else -> {
                                                UiText.Raw(
                                                    formatMoney(
                                                        cheapest.amount.amount,
                                                        cheapest.amount.currency,
                                                    ),
                                                )
                                            }
                                        }
                                    },
                                stateLabel =
                                    when (state) {
                                        SlotLiveStateUiModel.Upcoming -> {
                                            null
                                        }

                                        is SlotLiveStateUiModel.StartingSoon -> {
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
                                slot =
                                    SlotSegmentUiModel(
                                        id = slot.id,
                                        timeText =
                                            "${slot.start.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                                                slot.end.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                                        state = state,
                                        barStart =
                                            if (hasAxis && axisFrom != null) {
                                                (((slot.start - midnight) - axisFrom) / axisSpan)
                                                    .toFloat()
                                                    .coerceIn(0f, 1f)
                                            } else {
                                                0f
                                            },
                                        barEnd =
                                            if (hasAxis && axisFrom != null) {
                                                (((slot.end - midnight) - axisFrom) / axisSpan)
                                                    .toFloat()
                                                    .coerceIn(0f, 1f)
                                            } else {
                                                1f
                                            },
                                    ),
                            )
                        },
                )
            },
        wishlistCount = loaded.wishlistCount,
        // Only the timeline can be empty here. The Wishlist tile is the way to the other half and is
        // drawn whatever its count, so an empty Plan must not take it off the screen with it.
        emptyMessage = if (loaded.days.isEmpty()) UiText.Resource(Res.string.mon_yadlo_empty) else null,
    )
}
