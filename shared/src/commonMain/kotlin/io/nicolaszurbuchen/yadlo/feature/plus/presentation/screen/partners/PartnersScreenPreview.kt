package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

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
 * Two tiers, one of them four wide so the short last row is visible: its cards must stay the width
 * of the three above rather than stretching to fill.
 */
private class PartnersStateProvider : PreviewParameterProvider<PartnersUiModel> {
    override val values =
        sequenceOf(
            PartnersUiModel(isLoading = true, tiers = emptyList(), emptyMessage = null, noWebsiteNotice = null),
            published(),
        )
}

@Preview
@Composable
private fun PartnersScreenPreview(
    @PreviewParameter(PartnersStateProvider::class) state: PartnersUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PartnersScreen(state = state, onBackClick = {}, onPartnerClick = {})
        }
    }
}

private fun published() =
    PartnersUiModel(
        isLoading = false,
        emptyMessage = null,
        noWebsiteNotice = null,
        tiers =
            listOf(
                PartnerTierUiModel(
                    id = "cygnes-or",
                    name = "Cygnes d'or",
                    members =
                        listOf(
                            partner("totem", "Totem Escalade"),
                            partner("commune", "Commune de Préverenges"),
                            // No website — the tap says so rather than doing nothing.
                            PartnerUiModel(id = "edifice", name = "Edifice", logoUrl = null, url = null),
                            partner("mbc", "MBC"),
                        ),
                ),
                PartnerTierUiModel(
                    id = "partenaires",
                    name = "Partenaires",
                    members =
                        listOf(
                            partner("vivi-kola", "Vivi Kola"),
                            partner("grano-mate", "Grano Maté"),
                            partner("swanwine", "SwanWine"),
                        ),
                ),
            ),
    )

private fun partner(
    id: String,
    name: String,
) = PartnerUiModel(id = id, name = name, logoUrl = null, url = "https://example.ch/$id")
