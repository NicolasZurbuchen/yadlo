package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

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
import yadlo.shared.generated.resources.plus_assistance_subtitle
import yadlo.shared.generated.resources.plus_payment_no_cash
import yadlo.shared.generated.resources.plus_stands_count

/**
 * The full card beside a thinned one. The second is what a rolled-back publish looks like — no
 * transport, no accessibility, no FAQ — and the card has to hold its shape when three of its seven
 * rows are simply not there.
 */
private class PlusStateProvider : PreviewParameterProvider<PlusUiModel> {
    override val values =
        sequenceOf(
            PlusUiModel(isLoading = true, groups = emptyList()),
            published(),
            thinned(),
        )
}

@Preview
@Composable
private fun PlusScreenPreview(
    @PreviewParameter(PlusStateProvider::class) state: PlusUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PlusScreen(state = state, onEntryClick = {})
        }
    }
}

private fun published() =
    PlusUiModel(
        isLoading = false,
        groups =
            listOf(
                PlusGroupUiModel(
                    id = PlusGroupUiId.ON_SITE,
                    rows =
                        listOf(
                            PlusRowUiModel(
                                entry = PlusEntry.STANDS_FOOD,
                                subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(6)),
                            ),
                            PlusRowUiModel(
                                entry = PlusEntry.STANDS_MAKERS,
                                subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(2)),
                            ),
                            PlusRowUiModel(
                                entry = PlusEntry.PAYMENT,
                                subtitle = UiText.Resource(Res.string.plus_payment_no_cash),
                            ),
                            PlusRowUiModel(entry = PlusEntry.ACCESS, subtitle = null),
                            PlusRowUiModel(entry = PlusEntry.ACCESSIBILITY, subtitle = null),
                            PlusRowUiModel(entry = PlusEntry.HOURS, subtitle = null),
                            PlusRowUiModel(
                                entry = PlusEntry.ASSISTANCE,
                                subtitle = UiText.Resource(Res.string.plus_assistance_subtitle),
                            ),
                            PlusRowUiModel(entry = PlusEntry.FAQ, subtitle = null),
                        ),
                ),
            ),
    )

private fun thinned() =
    PlusUiModel(
        isLoading = false,
        groups =
            listOf(
                PlusGroupUiModel(
                    id = PlusGroupUiId.ON_SITE,
                    rows =
                        listOf(
                            // A row with a subtitle beside one without: the second must not be
                            // shorter, or a card of sixteen combs up and down as the content
                            // publishes one line here and two there.
                            PlusRowUiModel(
                                entry = PlusEntry.STANDS_FOOD,
                                subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(6)),
                            ),
                            PlusRowUiModel(entry = PlusEntry.HOURS, subtitle = null),
                        ),
                ),
            ),
    )
