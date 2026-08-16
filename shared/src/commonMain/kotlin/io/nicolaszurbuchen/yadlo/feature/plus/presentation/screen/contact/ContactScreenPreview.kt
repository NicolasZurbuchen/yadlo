package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

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

private class ContactStateProvider : PreviewParameterProvider<ContactUiModel> {
    override val values =
        sequenceOf(
            ContactUiModel(
                isLoading = true,
                emails = emptyList(),
                address = null,
                emptyMessage = null,
            ),
            published(),
        )
}

@Preview
@Composable
private fun ContactScreenPreview(
    @PreviewParameter(ContactStateProvider::class) state: ContactUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            ContactScreen(state = state, onBackClick = {}, onEmailClick = {})
        }
    }
}

private fun published() =
    ContactUiModel(
        isLoading = false,
        emptyMessage = null,
        emails =
            listOf(
                ContactEmailUiModel(id = "hello", label = "Informations générales", address = "hello@yadlo.ch"),
                ContactEmailUiModel(id = "musique", label = "Programmation musicale", address = "musique@yadlo.ch"),
                ContactEmailUiModel(id = "foodtrucks", label = "Food trucks", address = "foodtrucks@yadlo.ch"),
            ),
        address = "Avenue de la Plage 1\n1028 Préverenges\nSuisse",
    )
