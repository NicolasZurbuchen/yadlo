package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

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

/**
 * One question, which is what 2026 publishes. Two are shown because a header set in the display
 * face has to hold a long question as well as a short one.
 */
private class FaqStateProvider : PreviewParameterProvider<FaqUiModel> {
    override val values =
        sequenceOf(
            FaqUiModel(isLoading = true, entries = emptyList(), emptyMessage = null),
            published(),
        )
}

@Preview
@Composable
private fun FaqScreenPreview(
    @PreviewParameter(FaqStateProvider::class) state: FaqUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            FaqScreen(state = state, onBackClick = {})
        }
    }
}

private fun published() =
    FaqUiModel(
        isLoading = false,
        emptyMessage = null,
        entries =
            listOf(
                FaqEntryUiModel(
                    id = "entree",
                    question = "L'entrée est-elle payante ?",
                    answer =
                        "Non. L'entrée du festival est libre et gratuite, les trois jours. Seules certaines " +
                            "activités sont payantes, et leur prix est indiqué sur leur fiche.",
                ),
                FaqEntryUiModel(
                    id = "especes",
                    question = "Puis-je payer en espèces ?",
                    answer = "Non, le site est entièrement sans espèces. Carte ou TWINT.",
                ),
            ),
    )
