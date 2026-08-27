package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The four questions 2026 publishes, plus the skeleton they arrive into.
 *
 * A header set in the display face has to hold a long question as well as a short one, and *Comment
 * se protéger de la chaleur ?* against *Y a-t-il des boissons sans alcool ?* is that test with real
 * words rather than lorem.
 */
private class FaqScreenStateProvider : PreviewParameterProvider<FaqUiModel> {
    override val values =
        sequenceOf(
            FaqUiModel(isLoading = true, entries = emptyList(), emptyMessage = null),
            published(),
        )

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
                        id = "eau-potable",
                        question = "Y a-t-il de l'eau potable gratuite sur le site ?",
                        answer =
                            "Oui. La Goutte est un robinet d'eau fraîche, accessible gratuitement et en tout temps. " +
                                "Prenez une gourde : c'est le moyen le plus simple de tenir une journée entière au " +
                                "bord du lac.",
                    ),
                    FaqEntryUiModel(
                        id = "canicule",
                        question = "Comment se protéger de la chaleur ?",
                        answer =
                            "Le site est prévu pour : une grande tente offre un maximum d'ombre, des brumisateurs " +
                                "sont installés sur le site, et La Goutte donne accès à de l'eau fraîche gratuitement.",
                    ),
                    FaqEntryUiModel(
                        id = "sans-alcool",
                        question = "Y a-t-il des boissons sans alcool ?",
                        answer =
                            "Oui, et pas seulement de l'eau. Une sélection de boissons locales et rafraîchissantes " +
                                "est proposée avec nos partenaires Vivi Kola, Kosmos, Grano Mate et Supernatural Club.",
                    ),
                ),
        )
}

@PreviewThemes
@Composable
private fun FaqScreenPreview(
    @PreviewParameter(FaqScreenStateProvider::class) state: FaqUiModel,
) {
    YadloPreview {
        FaqScreen(state = state, onBackClick = {})
    }
}
