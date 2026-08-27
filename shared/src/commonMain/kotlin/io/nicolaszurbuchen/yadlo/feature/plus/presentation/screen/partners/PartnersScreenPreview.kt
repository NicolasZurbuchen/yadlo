package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * One tier of each shape. The first is drawn two across because it is first, with three members so
 * its short last row is visible — those cards must stay the width of the two above rather than
 * stretching to fill — and the second three across with four, for the same reason one step down.
 *
 * The logos are named against the published bank so the aspect ratios are the real ones: Volt-A is
 * all but square, VSM is six times wider than tall, and how those two come out beside each other is
 * the whole question [logoScaleFor] exists to answer. The tooling has no network, so what a preview
 * actually shows is the name fallback — which is the other thing worth looking at.
 */
private class PartnersScreenStateProvider : PreviewParameterProvider<PartnersUiModel> {
    override val values =
        sequenceOf(
            PartnersUiModel(isLoading = true, tiers = emptyList(), emptyMessage = null, noWebsiteNotice = null),
            published(),
        )

    private fun published() =
        PartnersUiModel(
            isLoading = false,
            emptyMessage = null,
            noWebsiteNotice = null,
            tiers =
                listOf(
                    PartnerTierUiModel(
                        id = "cygnes-or",
                        name = "Sponsors cygnes d'or",
                        members =
                            listOf(
                                partner("volt-a", "Volt-A Solutions Électriques", "webp"),
                                partner("vsm-nettoyage", "VSM Nettoyage & Services", "webp"),
                                // No website and no logo: the tap says so rather than doing nothing, and
                                // the card falls back to the name the logo was standing for.
                                PartnerUiModel(id = "edifice", name = "Edifice", logoUrl = null, url = null),
                            ),
                    ),
                    PartnerTierUiModel(
                        id = "partenaires",
                        name = "Partenaires",
                        members =
                            listOf(
                                partner("vivi-kola", "Vivi Kola", "svg"),
                                partner("grano-mate", "Grano Maté", "webp"),
                                partner("swan", "SwanWine", "webp"),
                                partner("mbc", "MBC — Transports de la région Morges-Bière-Cossonay", "svg"),
                            ),
                    ),
                ),
        )

    private fun partner(
        id: String,
        name: String,
        extension: String,
    ) = PartnerUiModel(
        id = id,
        name = name,
        logoUrl = "$BANK/$id.$extension",
        url = "https://example.ch/$id",
    )

    companion object {
        /** Where the published bank lives, so a preview names the file the running app actually fetches. */
        private const val BANK = "https://nicolaszurbuchen.github.io/yadlo/shared/logos"
    }
}

/**
 * The dark rendering is the half this screen was designed against. Every other card in the app
 * takes its ground from the theme; these thirty-nine are fixed white in both, because that is the
 * ground the logos were drawn for — so it is the dark one that shows whether a wall of white cards
 * on a near-black page reads as deliberate rather than as a rendering fault.
 */
@PreviewThemes
@Composable
private fun PartnersScreenPreview(
    @PreviewParameter(PartnersScreenStateProvider::class) state: PartnersUiModel,
) {
    YadloPreview {
        PartnersScreen(state = state, onBackClick = {}, onPartnerClick = {})
    }
}
