package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.partners_empty
import yadlo.shared.generated.resources.partners_no_website

fun PartnersState.toUiModel(): PartnersUiModel {
    val notice =
        PartnersNoticeUiModel(
            token = noWebsiteTaps,
            message = UiText.Resource(Res.string.partners_no_website),
        ).takeIf { noWebsiteTaps > 0 }

    val loaded =
        tiers ?: return PartnersUiModel(
            isLoading = true,
            tiers = emptyList(),
            emptyMessage = null,
            noWebsiteNotice = notice,
        )

    return PartnersUiModel(
        isLoading = false,
        tiers =
            loaded.map { tier ->
                PartnerTierUiModel(
                    id = tier.id,
                    // "Cygnes d'or" as the association writes it. The tiers are named after
                    // Préverenges' swans and no rewording of them would be an improvement.
                    name = tier.name,
                    members =
                        tier.members.map { partner ->
                            PartnerUiModel(
                                id = partner.id,
                                name = partner.name,
                                logoUrl = partner.logo?.url,
                                url = partner.url,
                            )
                        },
                )
            },
        emptyMessage = if (loaded.isEmpty()) UiText.Resource(Res.string.partners_empty) else null,
        noWebsiteNotice = notice,
    )
}
