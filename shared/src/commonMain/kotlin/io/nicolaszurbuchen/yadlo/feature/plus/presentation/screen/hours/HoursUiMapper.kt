package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.format.formatAsTimeOfDay
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.hours_empty
import yadlo.shared.generated.resources.hours_estimated

fun HoursState.toUiModel(): HoursUiModel {
    val loaded =
        days ?: return HoursUiModel(
            isLoading = true,
            days = emptyList(),
            caveat = null,
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
                )
            },
        caveat =
            UiText.Resource(Res.string.hours_estimated).takeIf { loaded.any { !it.hoursAreConfirmed } },
        emptyMessage = if (loaded.isEmpty()) UiText.Resource(Res.string.hours_empty) else null,
    )
}
