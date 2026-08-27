package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.design.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_about_unofficial
import yadlo.shared.generated.resources.plus_assistance_subtitle
import yadlo.shared.generated.resources.plus_partners_count
import yadlo.shared.generated.resources.plus_payment_no_cash
import yadlo.shared.generated.resources.plus_stands_count

/**
 * Three states, chosen for what they stress rather than for how much content they carry.
 *
 * *Loading* is the skeleton, which is a screen now rather than a spinner. *One group* is the tab at
 * its narrowest — a single card, nothing under it to give the layout a second edge, and the
 * networks having to sit correctly beneath one short card rather than a screenful. *Three groups*
 * is the interesting one: two rows, then **one**, then three. A card of one is where a grouped list
 * usually falls apart, and rows with and without a subtitle sit side by side in it, so the equal
 * heights are something you can see rather than something the code claims.
 */
private class PlusScreenStateProvider : PreviewParameterProvider<PlusUiModel> {
    override val values =
        sequenceOf(
            PlusUiModel(isLoading = true, groups = emptyList(), socials = emptyList()),
            oneGroup(),
            threeGroups(),
        )

    private fun oneGroup() =
        PlusUiModel(
            isLoading = false,
            groups =
                listOf(
                    PlusGroupUiModel(
                        id = PlusGroupIdUiModel.ON_SITE,
                        rows =
                            listOf(
                                PlusRowUiModel(
                                    entry = PlusEntryUiModel.STANDS_FOOD,
                                    subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(6)),
                                ),
                                PlusRowUiModel(
                                    entry = PlusEntryUiModel.PAYMENT,
                                    subtitle = UiText.Resource(Res.string.plus_payment_no_cash),
                                ),
                                PlusRowUiModel(entry = PlusEntryUiModel.HOURS, subtitle = null),
                            ),
                    ),
                ),
            socials = socials(),
        )

    private fun threeGroups() =
        PlusUiModel(
            isLoading = false,
            groups =
                listOf(
                    PlusGroupUiModel(
                        id = PlusGroupIdUiModel.ON_SITE,
                        rows =
                            listOf(
                                // A row with a subtitle beside one without: the second must not be
                                // shorter, or a card of sixteen combs up and down as the content
                                // publishes one line here and two there.
                                PlusRowUiModel(
                                    entry = PlusEntryUiModel.STANDS_FOOD,
                                    subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(6)),
                                ),
                                PlusRowUiModel(entry = PlusEntryUiModel.HOURS, subtitle = null),
                            ),
                    ),
                    PlusGroupUiModel(
                        id = PlusGroupIdUiModel.FESTIVAL,
                        rows =
                            listOf(
                                PlusRowUiModel(
                                    entry = PlusEntryUiModel.PARTNERS,
                                    subtitle = UiText.Resource(Res.string.plus_partners_count, listOf(24)),
                                ),
                            ),
                    ),
                    PlusGroupUiModel(
                        id = PlusGroupIdUiModel.APP,
                        rows =
                            listOf(
                                PlusRowUiModel(
                                    entry = PlusEntryUiModel.ABOUT,
                                    subtitle = UiText.Resource(Res.string.plus_about_unofficial),
                                ),
                                // The mail mark against the chevrons above it, which is the only place
                                // the difference between the two is readable at a glance.
                                PlusRowUiModel(entry = PlusEntryUiModel.REPORT, subtitle = null),
                                PlusRowUiModel(
                                    entry = PlusEntryUiModel.ASSISTANCE,
                                    subtitle = UiText.Resource(Res.string.plus_assistance_subtitle),
                                ),
                            ),
                    ),
                ),
            socials = socials(),
        )

    private fun socials() =
        listOf(
            SocialLinkUiModel("instagram", UiText.Raw("Instagram"), socialIconFor("instagram"), "https://example.com/i"),
            SocialLinkUiModel("facebook", UiText.Raw("Facebook"), socialIconFor("facebook"), "https://example.com/f"),
            SocialLinkUiModel("youtube", UiText.Raw("YouTube"), socialIconFor("youtube"), "https://example.com/y"),
            SocialLinkUiModel("tiktok", UiText.Raw("TikTok"), socialIconFor("tiktok"), "https://example.com/t"),
        )
}

/**
 * The dark rendering carries this file's real question: this screen is the app's densest use of
 * `surface` on `background`, and the two sit a few percent apart in the dark theme where they are
 * plainly different in the light one. If the cards ever stop reading as raised it shows here first
 * — and so does a skeleton block tuned against a light ground.
 */
@PreviewThemes
@Composable
private fun PlusScreenPreview(
    @PreviewParameter(PlusScreenStateProvider::class) state: PlusUiModel,
) {
    YadloPreview {
        PlusScreen(state = state, onEntryClick = {}, onSocialClick = {})
    }
}
