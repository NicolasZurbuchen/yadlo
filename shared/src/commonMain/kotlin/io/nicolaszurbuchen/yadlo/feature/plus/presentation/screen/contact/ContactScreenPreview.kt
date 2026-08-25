package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The skeleton and the directory. `hello@` is kept in the published state on purpose: it is the one
 * address with nobody behind it, so it is the tile that proves the sublabel still reads when the
 * name is missing.
 */
private class ContactScreenStateProvider : PreviewParameterProvider<ContactUiModel> {
    override val values =
        sequenceOf(
            ContactUiModel(isLoading = true, emails = emptyList(), address = null),
            published(),
        )

    private fun published() =
        ContactUiModel(
            isLoading = false,
            emails =
                listOf(
                    PlusEmailUiModel(
                        id = "hello",
                        label = "Informations générales",
                        responsible = null,
                        address = "hello@yadlo.ch",
                    ),
                    PlusEmailUiModel(
                        id = "musique",
                        label = "Programmation musicale",
                        responsible = "Jeremy B.",
                        address = "musique@yadlo.ch",
                    ),
                    PlusEmailUiModel(
                        id = "foodtrucks",
                        label = "Food trucks",
                        responsible = "Jeremy R.",
                        address = "foodtrucks@yadlo.ch",
                    ),
                ),
            address = "Avenue de la Plage 1\n1028 Préverenges\nSuisse",
        )
}

@PreviewThemes
@Composable
private fun ContactScreenPreview(
    @PreviewParameter(ContactScreenStateProvider::class) state: ContactUiModel,
) {
    YadloPreview {
        ContactScreen(state = state, onBackClick = {}, onEmailClick = {})
    }
}
