package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

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

/** Three of the six modes: prose only, prose with timetables, and the night with its two blocks. */
private class AccessStateProvider : PreviewParameterProvider<AccessUiModel> {
    override val values =
        sequenceOf(
            AccessUiModel(isLoading = true, modes = emptyList(), emptyMessage = null),
            published(),
        )
}

@Preview
@Composable
private fun AccessScreenPreview(
    @PreviewParameter(AccessStateProvider::class) state: AccessUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            AccessScreen(state = state, onBackClick = {}, onLinkClick = {})
        }
    }
}

private fun published() =
    AccessUiModel(
        isLoading = false,
        emptyMessage = null,
        modes =
            listOf(
                AccessModeUiModel(
                    id = "bus",
                    name = "En bus",
                    body = "Lignes 701 et 705, arrêt Préverenges, Village. Cinq minutes à pied jusqu'à la plage.",
                    links =
                        listOf(
                            AccessLinkUiModel(
                                id = "701",
                                label = "Horaires ligne 701",
                                sublabel = "PDF · MBC",
                                url = "https://example.ch/701.pdf",
                            ),
                        ),
                    nights = emptyList(),
                ),
                AccessModeUiModel(
                    id = "bus-nuit",
                    name = "Bus de nuit",
                    body = "Vers Morges, gare, avec correspondance pour Lausanne. Offerts par les MBC.",
                    links = emptyList(),
                    nights =
                        listOf(
                            AccessNightUiModel(
                                id = "vendredi",
                                night = "Vendredi",
                                times = "01:30 · 02:00",
                                notes = emptyList(),
                            ),
                            AccessNightUiModel(
                                id = "samedi",
                                night = "Samedi",
                                times = "00:59 · 01:30 · 02:00 · 02:30 · 03:00",
                                notes = listOf("03:00 — Pas de correspondance pour Lausanne."),
                            ),
                        ),
                ),
                AccessModeUiModel(
                    id = "bateau",
                    name = "Par le lac",
                    body =
                        "C'est un festival de plage : on peut arriver par l'eau, à la nage ou à la rame. " +
                            "Prévoyez de quoi payer votre bière une fois sec — le site est sans espèces.",
                    links = emptyList(),
                    nights = emptyList(),
                ),
            ),
    )
