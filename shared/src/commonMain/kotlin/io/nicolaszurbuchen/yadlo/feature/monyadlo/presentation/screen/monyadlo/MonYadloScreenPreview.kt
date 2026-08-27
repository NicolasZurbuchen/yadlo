package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_empty
import yadlo.shared.generated.resources.month_july
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running

/**
 * The three states that matter: loading, a Saturday evening mid-festival, and the eleven months when
 * nothing has been saved yet.
 *
 * The middle one is the case worth looking at — a finished afternoon above a running set above what
 * is still to come, which is what a Plan looks like at 21:00 on the Saturday.
 */
private class MonYadloScreenStateProvider : PreviewParameterProvider<MonYadloUiModel> {
    override val values =
        sequenceOf(
            MonYadloUiModel(
                isLoading = true,
                scale = null,
                days = emptyList(),
                wishlistCount = 0,
                emptyMessage = null,
            ),
            saturdayEvening(),
            nothingSavedYet(),
        )

    private fun saturdayEvening() =
        MonYadloUiModel(
            isLoading = false,
            // The three days laid over one another: 12:00 is the earliest any of them opens and 03:00
            // the latest any of them closes, so Friday's 17:00 set starts a third of the way in.
            scale = SlotScaleUiModel(startText = "12:00", middleText = "19:30", endText = "03:00"),
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
                                    barStart = 0.333f,
                                    barEnd = 0.433f,
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
                                    barStart = 0.133f,
                                    barEnd = 0.333f,
                                ),
                                row(
                                    id = "2026:caesure-sat",
                                    happeningId = "caesure",
                                    name = "Caesure",
                                    timeText = "20:30 – 22:00",
                                    stateLabel = UiText.Resource(Res.string.slot_state_running),
                                    state = SlotLiveStateUiModel.Running(progress = 0.35f),
                                    barStart = 0.567f,
                                    barEnd = 0.667f,
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
                                    barStart = 0.667f,
                                    barEnd = 0.933f,
                                ),
                            ),
                    ),
                ),
        )

    private fun nothingSavedYet() =
        MonYadloUiModel(
            isLoading = false,
            // No days, so no axis: a scale over nothing describes nothing.
            scale = null,
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
        barStart: Float,
        barEnd: Float,
        categoryId: String = "musique",
        categoryName: String = "Musique",
        priceText: UiText? = null,
    ) = MonYadloRowUiModel(
        id = id,
        happeningId = happeningId,
        name = name,
        categoryId = categoryId,
        categoryName = categoryName,
        priceText = priceText,
        stateLabel = stateLabel,
        slot =
            SlotSegmentUiModel(
                id = id,
                timeText = timeText,
                state = state,
                barStart = barStart,
                barEnd = barEnd,
            ),
    )
}

@PreviewThemes
@Composable
private fun MonYadloScreenPreview(
    @PreviewParameter(MonYadloScreenStateProvider::class) state: MonYadloUiModel,
) {
    YadloPreview {
        MonYadloScreen(state = state, onSlotClick = {}, onWishlistClick = {})
    }
}
