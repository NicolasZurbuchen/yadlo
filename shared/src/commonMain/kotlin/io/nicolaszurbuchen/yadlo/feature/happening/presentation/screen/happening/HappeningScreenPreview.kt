package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

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
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryTagUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.dietary_all_dairy_free
import yadlo.shared.generated.resources.dietary_all_vegan
import yadlo.shared.generated.resources.dietary_mark_dairy_free
import yadlo.shared.generated.resources.dietary_mark_gluten_free
import yadlo.shared.generated.resources.dietary_mark_vegan
import yadlo.shared.generated.resources.dietary_some_gluten_free
import yadlo.shared.generated.resources.happening_booking_action
import yadlo.shared.generated.resources.happening_fact_equipment_provided
import yadlo.shared.generated.resources.happening_link_website
import yadlo.shared.generated.resources.happening_price_deposit
import yadlo.shared.generated.resources.month_july
import yadlo.shared.generated.resources.slot_state_running

/**
 * The three kinds of Happening through the one template, plus the two states that are not a fiche.
 *
 * The Silent Party is the demanding case — two tiers, a deposit with a note, and a booking page —
 * and Vegan Fabrik is the other one, a stand whose menu is longer than everything above it. An
 * artist is what most fiches actually look like: a paragraph, some genres and a row of links.
 *
 * Written out rather than mapped from a HappeningState, because a preview may not import the domain
 * layer and that is where HappeningDetail lives.
 */
private class HappeningStateProvider : PreviewParameterProvider<HappeningUiModel> {
    override val values =
        sequenceOf(
            blank(isLoading = true),
            artist(),
            activity(),
            stand(),
            blank(isMissing = true),
        )
}

@Preview
@Composable
private fun HappeningScreenPreview(
    @PreviewParameter(HappeningStateProvider::class) state: HappeningUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            HappeningScreen(
                state = state,
                onBackClick = {},
                onLinkClick = {},
                onSlotHeartClick = {},
                onWishlistHeartClick = {},
            )
        }
    }
}

private fun blank(
    isLoading: Boolean = false,
    isMissing: Boolean = false,
) = HappeningUiModel(
    isLoading = isLoading,
    isMissing = isMissing,
    title = "",
    categoryId = "",
    categoryLabel = "",
    imageUrl = null,
    description = null,
    tags = emptyList(),
    dietary = emptyList(),
    slots = emptyList(),
    price = null,
    booking = null,
    facts = emptyList(),
    menu = emptyList(),
    links = emptyList(),
    wishlisted = null,
)

private fun artist() =
    blank().copy(
        title = "DJ ALF",
        categoryId = "musique",
        categoryLabel = "MUSIQUE",
        // A real published address, so the preview stands on the Category fill the header falls back
        // to rather than on a hole. Nothing is fetched here — the tooling has no network — which is
        // exactly the state a visitor with no signal sees.
        imageUrl = "https://nicolaszurbuchen.github.io/yadlo/shared/images/artists/alf.webp",
        description =
            "Résident hebdomadaire du MAD Club et du D! Club, il a joué au Montreux Jazz, au Venoge " +
                "et à Caribana. Un répertoire volontairement large, résumé par sa devise : « Bangers Only ».",
        tags = listOf("Commercial", "Afro house", "House'n'disco"),
        slots =
            listOf(
                HappeningSlotUiModel(
                    id = "2026:dj-alf-fri",
                    dayName = "Vendredi",
                    dayNumber = "10",
                    monthName = UiText.Resource(Res.string.month_july),
                    timeText = "17:00 – 18:30",
                    stateLabel = UiText.Resource(Res.string.slot_state_running),
                    state = SlotLiveStateUiModel.Running(progress = 0.4f),
                    planned = true,
                ),
            ),
        links =
            listOf(
                SocialLinkUiModel(
                    id = "website",
                    name = UiText.Resource(Res.string.happening_link_website),
                    icon = socialIconFor("website"),
                    url = "https://djalf.ch/",
                ),
                SocialLinkUiModel(
                    id = "instagram",
                    name = UiText.Raw("Instagram"),
                    icon = socialIconFor("instagram"),
                    url = "https://instagram.com/",
                ),
                SocialLinkUiModel(
                    id = "soundcloud",
                    name = UiText.Raw("SoundCloud"),
                    icon = socialIconFor("soundcloud"),
                    url = "https://soundcloud.com/",
                ),
            ),
    )

private fun activity() =
    blank().copy(
        title = "Silent Party",
        categoryId = "silent",
        categoryLabel = "SILENT PARTY",
        description = "Trois ambiances, un seul dancefloor et des centaines de casques illuminés.",
        tags = listOf("All style"),
        slots =
            listOf(
                HappeningSlotUiModel(
                    id = "2026:silent-party-sat",
                    dayName = "Samedi",
                    dayNumber = "11",
                    monthName = UiText.Resource(Res.string.month_july),
                    timeText = "21:00 – 02:00",
                    stateLabel = null,
                    state = SlotLiveStateUiModel.Upcoming,
                    planned = false,
                ),
            ),
        price =
            HappeningPriceUiModel(
                tiers =
                    listOf(
                        HappeningPriceTierUiModel(label = "Adulte", amount = UiText.Raw("CHF 25")),
                        HappeningPriceTierUiModel(label = "Moins de 16 ans", amount = UiText.Raw("CHF 15")),
                    ),
                deposit = UiText.Resource(Res.string.happening_price_deposit, listOf("CHF 50")),
                depositNote = "Caution casque, préautorisée à la réservation et débitée à la remise du casque.",
            ),
        booking =
            HappeningBookingUiModel(
                label = UiText.Resource(Res.string.happening_booking_action),
                url = "https://discover.smeetz.com/",
            ),
        facts = listOf(UiText.Resource(Res.string.happening_fact_equipment_provided)),
    )

private fun stand() =
    blank().copy(
        title = "Vegan Fabrik",
        categoryId = "restauration",
        categoryLabel = "RESTAURATION",
        description = "Une cuisine 100 % végétale qui prouve qu'on peut allier gourmandise, créativité et qualité.",
        tags = listOf("Cuisine végétale"),
        dietary =
            listOf(
                YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_all_vegan),
                YadloDietaryTagUiModel(YadloDietaryMarkUiModel.DAIRY_FREE, Res.string.dietary_all_dairy_free),
                YadloDietaryTagUiModel(YadloDietaryMarkUiModel.GLUTEN_FREE, Res.string.dietary_some_gluten_free),
            ),
        menu =
            listOf(
                HappeningMenuGroupUiModel(
                    id = "plats",
                    name = "Plats",
                    description = null,
                    items =
                        listOf(
                            HappeningMenuItemUiModel(
                                name = "Assiette de mezzés",
                                priceText = "CHF 15",
                                description = "Houmous, caviar d’aubergine, falafels, pain plat.",
                                dietary =
                                    listOf(
                                        YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_mark_vegan),
                                        YadloDietaryTagUiModel(
                                            YadloDietaryMarkUiModel.DAIRY_FREE,
                                            Res.string.dietary_mark_dairy_free,
                                        ),
                                    ),
                            ),
                            HappeningMenuItemUiModel(
                                name = "Seitan à la cantonaise",
                                priceText = "CHF 18",
                                description = "Tofu suisse mijoté aux légumes de saison, riz.",
                                dietary =
                                    listOf(
                                        YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_mark_vegan),
                                        YadloDietaryTagUiModel(
                                            YadloDietaryMarkUiModel.GLUTEN_FREE,
                                            Res.string.dietary_mark_gluten_free,
                                        ),
                                        YadloDietaryTagUiModel(
                                            YadloDietaryMarkUiModel.DAIRY_FREE,
                                            Res.string.dietary_mark_dairy_free,
                                        ),
                                    ),
                            ),
                        ),
                ),
                // A second group, so the gap between two of them can be judged and the group
                // description has somewhere to be seen. Four or five is what the real cartes carry.
                HappeningMenuGroupUiModel(
                    id = "tartelettes",
                    name = "Tartelettes",
                    description = "Pâte sablée végétale, crème à l’aquafaba ou à la noix de cajou.",
                    items =
                        listOf(
                            HappeningMenuItemUiModel(
                                name = "Citron meringuée",
                                priceText = "CHF 7",
                                description = null,
                                dietary =
                                    listOf(
                                        YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_mark_vegan),
                                    ),
                            ),
                            // The longest name in the 2026 content beside the most marks any dish
                            // carries: the pair that decides whether the glyphs fit on the name's
                            // own line.
                            HappeningMenuItemUiModel(
                                name = "Chocolat noir et fleur de sel",
                                priceText = "CHF 7",
                                description = null,
                                dietary =
                                    listOf(
                                        YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_mark_vegan),
                                        YadloDietaryTagUiModel(
                                            YadloDietaryMarkUiModel.GLUTEN_FREE,
                                            Res.string.dietary_mark_gluten_free,
                                        ),
                                        YadloDietaryTagUiModel(
                                            YadloDietaryMarkUiModel.DAIRY_FREE,
                                            Res.string.dietary_mark_dairy_free,
                                        ),
                                    ),
                            ),
                        ),
                ),
            ),
        // A stand has no Slots of its own on this screen: its hours are the site's, and the
        // Programme already drops them for the same reason. Its heart therefore sits in the bar,
        // which is what a non-null `wishlisted` says.
        price = null,
        wishlisted = true,
        links =
            listOf(
                SocialLinkUiModel(
                    id = "website",
                    name = UiText.Resource(Res.string.happening_link_website),
                    icon = socialIconFor("website"),
                    url = "https://vegan-fabrik.ch/",
                ),
            ),
    )
