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

/**
 * The skeleton and the published block, which is the whole of this screen now that the empty state
 * is gone: there is always a payment block, and a null one is the bundle still landing.
 */
private class PaymentStateProvider : PreviewParameterProvider<PaymentUiModel> {
    override val values =
        sequenceOf(
            PaymentUiModel(
                isLoading = true,
                headline = null,
                summary = null,
                methods = emptyList(),
                notes = emptyList(),
            ),
            published(),
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

/**
 * The dark half, and the one this screen most needs. The hero is the app's only large use of the
 * bandeau blue as a ground, and it swaps ends between the themes — light draws dark ink on the
 * bright blue, dark draws bright ink on the deep one. The tinted ✕ against three ✓ is the other
 * thing that has to survive the swap.
 */
@Preview
@Composable
private fun PaymentScreenDarkPreview(
    @PreviewParameter(PaymentStateProvider::class) state: PaymentUiModel,
) {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PaymentScreen(state = state, onBackClick = {}, onLinkClick = {})
        }
    }
}

private fun published() =
    PaymentUiModel(
        isLoading = false,
        headline = "Carte et TWINT uniquement",
        summary = "Aucun stand n'accepte les espèces, bar compris.",
        methods =
            listOf(
                PaymentMethodUiModel(id = "carte", name = "Visa, Mastercard et Maestro — sans contact", accepted = true),
                PaymentMethodUiModel(id = "twint", name = "TWINT", accepted = true),
                PaymentMethodUiModel(id = "mobile", name = "Apple Pay et Google Pay", accepted = true),
                PaymentMethodUiModel(id = "especes", name = "Espèces", accepted = false),
            ),
        notes =
            listOf(
                PaymentNoteUiModel(
                    id = "pas-de-twint",
                    title = "Vous n'avez pas TWINT ?",
                    body =
                        "L'application dépend de votre banque : certaines ont la leur, d'autres passent par " +
                            "TWINT Prepaid. La page officielle vous oriente vers la bonne.",
                    links =
                        listOf(
                            PaymentLinkUiModel(
                                id = "twint",
                                label = "twint.ch",
                                sublabel = "Site officiel",
                                url = "https://www.twint.ch/",
                            ),
                        ),
                ),
                PaymentNoteUiModel(
                    id = "pourquoi",
                    title = "Pourquoi",
                    body =
                        "Sans caisse en liquide, les files avancent plus vite et l'équipe passe sa soirée à " +
                            "servir plutôt qu'à compter. C'est aussi plus sûr pour des bénévoles qui ferment " +
                            "le bar à trois heures du matin.",
                    links = emptyList(),
                ),
            ),
    )
