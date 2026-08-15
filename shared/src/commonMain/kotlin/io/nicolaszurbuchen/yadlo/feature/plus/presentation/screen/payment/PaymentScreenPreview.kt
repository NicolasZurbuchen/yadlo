package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

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
import yadlo.shared.generated.resources.payment_empty

private class PaymentStateProvider : PreviewParameterProvider<PaymentUiModel> {
    override val values =
        sequenceOf(
            PaymentUiModel(
                isLoading = true,
                accepted = emptyList(),
                refused = emptyList(),
                notes = emptyList(),
                links = emptyList(),
                emptyMessage = null,
            ),
            published(),
            // Reachable only through a restored back stack over a publish that dropped the section.
            // Drawn anyway, because a page that spins forever is a worse way to say so.
            PaymentUiModel(
                isLoading = false,
                accepted = emptyList(),
                refused = emptyList(),
                notes = emptyList(),
                links = emptyList(),
                emptyMessage = UiText.Resource(Res.string.payment_empty),
            ),
        )
}

@Preview
@Composable
private fun PaymentScreenPreview(
    @PreviewParameter(PaymentStateProvider::class) state: PaymentUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PaymentScreen(state = state, onBackClick = {}, onLinkClick = {})
        }
    }
}

private fun published() =
    PaymentUiModel(
        isLoading = false,
        accepted = listOf("Cartes Visa, Mastercard et Maestro", "TWINT"),
        refused = listOf("Espèces"),
        notes =
            listOf(
                "Aucun stand n'accepte les espèces, bar compris. Prévoyez une carte ou TWINT avant de venir.",
                "L'application TWINT dépend de votre banque : certaines ont la leur, d'autres passent par TWINT Prepaid.",
            ),
        links =
            listOf(
                PaymentLinkUiModel(
                    id = "twint",
                    label = "twint.ch",
                    sublabel = "Site officiel",
                    url = "https://www.twint.ch/",
                ),
            ),
        emptyMessage = null,
    )
