package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.slotLiveStateAt
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsDayOfMonth
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.ui.monthName
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_empty
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes

/**
 * The Plan, day by day, each row measured against the same clock the Programme reads.
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
            days = emptyList(),
            wishlistCount = 0,
            emptyMessage = null,
        )

    return MonYadloUiModel(
        isLoading = false,
        days =
            loaded.days.map { day ->
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
                                timeText =
                                    "${slot.start.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                                        slot.end.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
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
                                state = state,
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
