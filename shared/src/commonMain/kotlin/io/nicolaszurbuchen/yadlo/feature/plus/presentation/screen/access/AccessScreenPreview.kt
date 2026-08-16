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
import io.nicolaszurbuchen.yadlo.app.design.uimodel.FactMarkUiModel

/**
 * The skeleton, then the 2026 page whole. All three section shapes are in it: facts with links
 * (the bus), prose with a timetable (the night bus), facts with a caveat among them (the car), and
 * prose alone (the water).
 */
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

/**
 * The dark half. The ⓘ beside *places limitées* is the thing to check: it is the one mark on this
 * screen that has to stay legible as tertiary ink while reading as a different kind of statement
 * from the ✓ above it.
 */
@Preview
@Composable
private fun AccessScreenDarkPreview(
    @PreviewParameter(AccessStateProvider::class) state: AccessUiModel,
) {
    YadloTheme(darkTheme = true) {
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
                    name = "Venir en bus",
                    body = null,
                    facts =
                        listOf(
                            fact("lignes", "Lignes 701 et 705, arrêt Préverenges, Village"),
                            fact("marche", "Cinq minutes à pied jusqu'à la plage"),
                            fact("plancher", "Les deux lignes sont à plancher surbaissé"),
                        ),
                    links =
                        listOf(
                            AccessLinkUiModel(
                                id = "horaires-701",
                                label = "Horaires ligne 701",
                                sublabel = "PDF · MBC",
                                url = "https://example.ch/701.pdf",
                            ),
                            AccessLinkUiModel(
                                id = "horaires-705",
                                label = "Horaires ligne 705",
                                sublabel = "PDF · MBC",
                                url = "https://example.ch/705.pdf",
                            ),
                        ),
                    nights = emptyList(),
                ),
                AccessModeUiModel(
                    id = "bus-nuit",
                    name = "Rentrer de nuit",
                    body = "Les bus pyjama desservent Morges, gare, avec correspondance pour Lausanne. Offerts par les MBC.",
                    facts = emptyList(),
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
                    id = "voiture",
                    name = "En voiture",
                    body = null,
                    facts =
                        listOf(
                            AccessFactUiModel(
                                id = "places",
                                text = "Places limitées, et la distance jusqu'au site varie beaucoup",
                                mark = FactMarkUiModel.INFO,
                            ),
                            fact("reservees", "Deux places réservées près de l'entrée"),
                        ),
                    links =
                        listOf(
                            AccessLinkUiModel(
                                id = "parkings",
                                label = "Plan des parkings",
                                sublabel = "PDF",
                                url = "https://example.ch/parkings.pdf",
                            ),
                        ),
                    nights = emptyList(),
                ),
                AccessModeUiModel(
                    id = "velo-pied",
                    name = "À vélo, à pied",
                    body = null,
                    facts =
                        listOf(
                            fact("parking-velo", "Parking vélo à l'entrée du site"),
                            fact("depuis-morges", "35 minutes depuis Morges, le long du lac"),
                        ),
                    links = emptyList(),
                    nights = emptyList(),
                ),
                AccessModeUiModel(
                    id = "nage",
                    name = "À la nage",
                    body =
                        "C'est un festival de plage : on peut arriver par l'eau, à la nage ou à la rame. " +
                            "Prévoyez de quoi payer votre bière une fois sec — le site est sans espèces.",
                    facts = emptyList(),
                    links = emptyList(),
                    nights = emptyList(),
                ),
            ),
    )

private fun fact(
    id: String,
    text: String,
) = AccessFactUiModel(id = id, text = text, mark = FactMarkUiModel.CHECK)
