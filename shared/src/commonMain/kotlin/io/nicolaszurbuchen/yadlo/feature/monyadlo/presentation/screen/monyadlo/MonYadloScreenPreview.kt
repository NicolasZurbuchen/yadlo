package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.month_july
import yadlo.shared.generated.resources.mon_yadlo_empty
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running

/**
 * The three states that matter: loading, a Saturday evening mid-festival, and the eleven months when
 * nothing has been saved yet.
 *
 * The middle one is the case worth looking at — a finished afternoon above a running set above what
 * is still to come, which is what a Plan looks like at 21:00 on the Saturday.
 */
private class MonYadloStateProvider : PreviewParameterProvider<MonYadloUiModel> {
    override val values =
        sequenceOf(
            MonYadloUiModel(isLoading = true, days = emptyList(), wishlistCount = 0, emptyMessage = null),
            saturdayEvening(),
            nothingSavedYet(),
        )
}

@Preview
@Composable
private fun MonYadloScreenPreview(
    @PreviewParameter(MonYadloStateProvider::class) state: MonYadloUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            MonYadloScreen(state = state, onSlotClick = {}, onWishlistClick = {})
        }
    }
}

private fun saturdayEvening() =
    MonYadloUiModel(
        isLoading = false,
        wishlistCount = 3,
        emptyMessage = null,
        days =
            listOf(
                MonYadloDayUiModel(
                    id = "2026:fri",
                    name = "Vendredi",
                    dayNumber = "10",
                    monthName = UiText.Resource(Res.string.month_july),
                    rows =
                        listOf(
                            row(
                                id = "2026:dj-alf-fri",
                                happeningId = "dj-alf",
                                name = "DJ ALF",
                                timeText = "17:00 – 18:30",
                                stateLabel = UiText.Resource(Res.string.slot_state_over),
                                state = SlotLiveStateUiModel.Over,
                            ),
                        ),
                ),
                MonYadloDayUiModel(
                    id = "2026:sat",
                    name = "Samedi",
                    dayNumber = "11",
                    monthName = UiText.Resource(Res.string.month_july),
                    rows =
                        listOf(
                            row(
                                id = "2026:paddle-sat",
                                happeningId = "paddle",
                                name = "Initiation paddle",
                                categoryId = "eau",
                                categoryName = "Sur l'eau",
                                timeText = "14:00 – 17:00",
                                stateLabel = UiText.Resource(Res.string.slot_state_over),
                                state = SlotLiveStateUiModel.Over,
                            ),
                            row(
                                id = "2026:caesure-sat",
                                happeningId = "caesure",
                                name = "Caesure",
                                timeText = "20:30 – 22:00",
                                stateLabel = UiText.Resource(Res.string.slot_state_running),
                                state = SlotLiveStateUiModel.Running(progress = 0.35f),
                            ),
                            row(
                                id = "2026:silent-party-sat",
                                happeningId = "silent-party",
                                name = "Silent Party",
                                categoryId = "silent",
                                categoryName = "Silent Party",
                                timeText = "22:00 – 02:00",
                                stateLabel = null,
                                state = SlotLiveStateUiModel.Upcoming,
                            ),
                        ),
                ),
            ),
    )

private fun nothingSavedYet() =
    MonYadloUiModel(
        isLoading = false,
        days = emptyList(),
        wishlistCount = 0,
        emptyMessage = UiText.Resource(Res.string.mon_yadlo_empty),
    )

private fun row(
    id: String,
    happeningId: String,
    name: String,
    timeText: String,
    stateLabel: UiText?,
    state: SlotLiveStateUiModel,
    categoryId: String = "musique",
    categoryName: String = "Musique",
) = MonYadloRowUiModel(
    id = id,
    happeningId = happeningId,
    name = name,
    categoryId = categoryId,
    categoryName = categoryName,
    timeText = timeText,
    stateLabel = stateLabel,
    state = state,
)
