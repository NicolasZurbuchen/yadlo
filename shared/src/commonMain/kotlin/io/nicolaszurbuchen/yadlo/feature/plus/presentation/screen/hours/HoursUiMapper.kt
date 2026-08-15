package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.hours_before_opening
import yadlo.shared.generated.resources.hours_empty
import yadlo.shared.generated.resources.hours_estimated

fun HoursState.toUiModel(): HoursUiModel {
    val loaded =
        days ?: return HoursUiModel(
            isLoading = true,
            days = emptyList(),
            caveat = null,
            beforeOpeningNote = null,
            emptyMessage = null,
        )

    return HoursUiModel(
        isLoading = false,
        days =
            loaded.map { day ->
                OpeningDayUiModel(
                    id = day.id,
                    name = day.name,
                    // Resolved in Europe/Zurich, never the device zone: 16:00 reads 16:00 for
                    // everyone on the beach, whatever their phone thinks.
                    window =
                        "${day.opensAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                            day.closesAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE),
                    programme =
                        if (day.firstStartsAt != null && day.lastEndsAt != null) {
                            "${day.firstStartsAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)} – " +
                                day.lastEndsAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)
                        } else {
                            null
                        },
                )
            },
        caveat =
            UiText.Resource(Res.string.hours_estimated).takeIf { loaded.any { !it.hoursAreConfirmed } },
        // Said out loud rather than corrected. A Slot outside the window is legal and real, and a
        // screen that quietly clamped it would be telling the festival it is wrong about its site.
        beforeOpeningNote =
            UiText
                .Resource(Res.string.hours_before_opening)
                .takeIf { loaded.any { day -> day.firstStartsAt != null && day.firstStartsAt < day.opensAt } },
        emptyMessage = if (loaded.isEmpty()) UiText.Resource(Res.string.hours_empty) else null,
    )
}
