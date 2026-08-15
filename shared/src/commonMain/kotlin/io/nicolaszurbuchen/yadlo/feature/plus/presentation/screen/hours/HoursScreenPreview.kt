package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

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
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.hours_before_opening

/**
 * The real 2026 hours. Saturday is the one worth looking at: it closes at 03:00 the next morning
 * and its programme starts at 10:00, two hours before the site opens — both true, and both would
 * read as bugs if the screen did not say so.
 */
private class HoursStateProvider : PreviewParameterProvider<HoursUiModel> {
    override val values =
        sequenceOf(
            HoursUiModel(
                isLoading = true,
                days = emptyList(),
                caveat = null,
                beforeOpeningNote = null,
                emptyMessage = null,
            ),
            published(),
        )
}

@Preview
@Composable
private fun HoursScreenPreview(
    @PreviewParameter(HoursStateProvider::class) state: HoursUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            HoursScreen(state = state, onBackClick = {})
        }
    }
}

private fun published() =
    HoursUiModel(
        isLoading = false,
        days =
            listOf(
                OpeningDayUiModel(id = "2026:fri", name = "Vendredi", window = "16:00 – 02:00", programme = "17:00 – 01:30"),
                OpeningDayUiModel(id = "2026:sat", name = "Samedi", window = "12:00 – 03:00", programme = "10:00 – 02:30"),
                OpeningDayUiModel(id = "2026:sun", name = "Dimanche", window = "12:00 – 22:00", programme = "10:00 – 21:00"),
            ),
        caveat = null,
        beforeOpeningNote = UiText.Resource(Res.string.hours_before_opening),
        emptyMessage = null,
    )
