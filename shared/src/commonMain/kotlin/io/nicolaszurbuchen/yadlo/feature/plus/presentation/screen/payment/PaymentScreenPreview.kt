package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The skeleton and the published block, which is the whole of this screen now that the empty state
 * is gone: there is always a payment block, and a null one is the bundle still landing.
 */
private class PaymentScreenStateProvider : PreviewParameterProvider<PaymentUiModel> {
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
}

/**
 * The dark rendering is the one this screen most needs. The hero is the app's only large use of the
 * bandeau blue as a ground, and it swaps ends between the themes — light draws dark ink on the
 * bright blue, dark draws bright ink on the deep one. The tinted ✕ against three ✓ is the other
 * thing that has to survive the swap.
 */
@PreviewThemes
@Composable
private fun PaymentScreenPreview(
    @PreviewParameter(PaymentScreenStateProvider::class) state: PaymentUiModel,
) {
    YadloPreview {
        PaymentScreen(state = state, onBackClick = {}, onLinkClick = {})
    }
}
