package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

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
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.price_from
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.time.Duration.Companion.minutes

/**
 * The Saturday at 15:45, which is the moment the prototype was argued from: five things running,
 * two ending, Dubside fifteen minutes out and the morning already dimmed. Then the two empties.
 *
 * Written out rather than mapped from a ProgrammeState, because a preview may not import the domain
 * layer and that is where ProgrammeContent lives.
 */
private class ProgrammeStateProvider : PreviewParameterProvider<ProgrammeUiModel> {
    override val values =
        sequenceOf(
            // Before the first bundle reaches the screen.
            ProgrammeUiModel(
                isLoading = true,
                days = emptyList(),
                categories = emptyList(),
                scale = null,
                rows = emptyList(),
                emptyMessage = null,
            ),
            ProgrammeUiModel(
                isLoading = false,
                days = days(),
                categories = categories(),
                scale = ProgrammeScaleUiModel(startText = "10:00", middleText = "18:00", endText = "03:00"),
                rows = saturdayAtQuarterToFour(),
                emptyMessage = null,
            ),
            // A filter that matched nothing — the chips stay, since the way out is to change them.
            ProgrammeUiModel(
                isLoading = false,
                days = days(),
                categories = categories(selectedId = "silent"),
                scale = null,
                rows = emptyList(),
                emptyMessage = UiText.Resource(Res.string.programme_empty_filter),
            ),
            // Spring: dates published, nothing under them. No day chips, because switching days
            // cannot help and offering it reads as a screen that failed to load.
            ProgrammeUiModel(
                isLoading = false,
                days = emptyList(),
                categories = emptyList(),
                scale = null,
                rows = emptyList(),
                emptyMessage = UiText.Resource(Res.string.programme_empty_unpublished),
            ),
        )

    private fun days() =
        listOf(
            DayChipUiModel(id = "2026:fri", name = "Vendredi", isSelected = false),
            DayChipUiModel(id = "2026:sat", name = "Samedi", isSelected = true),
            DayChipUiModel(id = "2026:sun", name = "Dimanche", isSelected = false),
        )

    private fun categories(selectedId: String? = null) =
        listOf(
            CategoryChipUiModel(id = "musique", name = "Musique", isSelected = selectedId == "musique"),
            CategoryChipUiModel(id = "silent", name = "Silent Party", isSelected = selectedId == "silent"),
            CategoryChipUiModel(id = "eau", name = "Sur l'eau", isSelected = selectedId == "eau"),
            CategoryChipUiModel(id = "terre", name = "Sur terre", isSelected = selectedId == "terre"),
            CategoryChipUiModel(id = "enfants", name = "Enfants", isSelected = selectedId == "enfants"),
        )

    /** Bar fractions are measured against the Saturday axis in the scale above: 10:00 to 03:00. */
    private fun saturdayAtQuarterToFour() =
        listOf(
            row(
                id = "2026:acro-yoga-sat",
                name = "Acro-yoga",
                categoryId = "terre",
                categoryName = "Sur terre",
                priceText = UiText.Resource(Res.string.price_free),
                stateLabel = UiText.Resource(Res.string.slot_state_over),
                state = SlotLiveStateUiModel.Over,
                slots = listOf(segment("10:00 – 11:00", SlotLiveStateUiModel.Over, 0f, 0.059f)),
            ),
            row(
                id = "2026:thalasso-sat",
                name = "Thalassothérapie",
                categoryId = "musique",
                categoryName = "Musique",
                priceText = null,
                stateLabel = UiText.Resource(Res.string.slot_state_ending, listOf("15")),
                state = SlotLiveStateUiModel.Ending(endsIn = 15.minutes, progress = 0.875f),
                slots =
                    listOf(
                        segment(
                            "14:00 – 16:00",
                            SlotLiveStateUiModel.Ending(endsIn = 15.minutes, progress = 0.875f),
                            0.235f,
                            0.353f,
                        ),
                    ),
            ),
            row(
                id = "2026:gladiasup-sat",
                name = "GladiaSUP",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                priceText = UiText.Raw("CHF 5"),
                stateLabel = UiText.Resource(Res.string.slot_state_running),
                state = SlotLiveStateUiModel.Running(progress = 0.53f),
                slots =
                    listOf(
                        segment("12:00 – 19:00", SlotLiveStateUiModel.Running(progress = 0.53f), 0.118f, 0.529f),
                    ),
            ),
            // The case the merge exists for: three separate hours of one activity, the first over
            // while the row is not, on one track — DECISIONS.md § A row is a Happening on a day.
            row(
                id = "2026:sat/sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                priceText = UiText.Raw("CHF 20"),
                stateLabel = UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("45")),
                state = SlotLiveStateUiModel.StartingSoon(startsIn = 45.minutes),
                slots =
                    listOf(
                        segment("14:00 – 15:00", SlotLiveStateUiModel.Over, 0.235f, 0.294f),
                        segment("16:00 – 17:00", SlotLiveStateUiModel.StartingSoon(45.minutes), 0.353f, 0.412f),
                        segment("18:00 – 19:00", SlotLiveStateUiModel.Upcoming, 0.471f, 0.529f),
                    ),
            ),
            row(
                id = "2026:dubside-sat",
                name = "Dubside",
                categoryId = "musique",
                categoryName = "Musique",
                priceText = null,
                stateLabel = UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("15")),
                state = SlotLiveStateUiModel.StartingSoon(startsIn = 15.minutes),
                slots =
                    listOf(
                        segment("16:00 – 18:00", SlotLiveStateUiModel.StartingSoon(15.minutes), 0.353f, 0.471f),
                    ),
            ),
            row(
                id = "2026:silent-party-sat",
                name = "Silent Party",
                categoryId = "silent",
                categoryName = "Silent Party",
                // Two tiers, so the row shows the one that lets a family in rather than the adult
                // price — CHF 25 adulte, CHF 15 moins de 16 ans.
                priceText = UiText.Resource(Res.string.price_from, listOf("CHF 15")),
                stateLabel = null,
                state = SlotLiveStateUiModel.Upcoming,
                slots = listOf(segment("20:00 – 02:00", SlotLiveStateUiModel.Upcoming, 0.588f, 0.941f)),
            ),
            row(
                id = "2026:coin-enfant-sat",
                name = "Coin enfant — maquillage, bricolage et mur de grimpe",
                categoryId = "enfants",
                categoryName = "Enfants",
                priceText = UiText.Resource(Res.string.price_free),
                stateLabel = UiText.Resource(Res.string.slot_state_running),
                state = SlotLiveStateUiModel.Running(progress = 0.53f),
                slots =
                    listOf(
                        segment("12:00 – 19:00", SlotLiveStateUiModel.Running(progress = 0.53f), 0.118f, 0.529f),
                    ),
            ),
        )

    private fun row(
        id: String,
        name: String,
        categoryId: String,
        categoryName: String,
        priceText: UiText?,
        stateLabel: UiText?,
        state: SlotLiveStateUiModel,
        slots: List<SlotSegmentUiModel>,
    ) = SlotRowUiModel(
        id = id,
        happeningId = id.substringAfterLast('/').substringAfter(':').substringBeforeLast('-'),
        name = name,
        categoryId = categoryId,
        categoryName = categoryName,
        priceText = priceText,
        stateLabel = stateLabel,
        state = state,
        slots = slots,
    )

    private fun segment(
        timeText: String,
        state: SlotLiveStateUiModel,
        barStart: Float,
        barEnd: Float,
    ) = SlotSegmentUiModel(
        id = timeText,
        timeText = timeText,
        state = state,
        barStart = barStart,
        barEnd = barEnd,
    )
}

/**
 * The Scaffold paints the ground under this screen, so a preview without it shows the rows floating
 * on whatever the tooling happens to use — which is not what anyone will see.
 */
@Composable
private fun ProgrammePreviewSurface(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
        content()
    }
}

@Preview
@Composable
private fun ProgrammeScreenLightPreview(
    @PreviewParameter(ProgrammeStateProvider::class) state: ProgrammeUiModel,
) {
    YadloTheme(darkTheme = false) {
        ProgrammePreviewSurface {
            ProgrammeScreen(
                state = state,
                onDayClick = {},
                onCategoryClick = {},
                onAllCategoriesClick = {},
                onSlotClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun ProgrammeScreenDarkPreview(
    @PreviewParameter(ProgrammeStateProvider::class) state: ProgrammeUiModel,
) {
    YadloTheme(darkTheme = true) {
        ProgrammePreviewSurface {
            ProgrammeScreen(
                state = state,
                onDayClick = {},
                onCategoryClick = {},
                onAllCategoriesClick = {},
                onSlotClick = {},
            )
        }
    }
}
