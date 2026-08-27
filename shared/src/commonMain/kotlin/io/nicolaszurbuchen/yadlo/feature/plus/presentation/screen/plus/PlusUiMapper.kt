package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.design.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_about_unofficial
import yadlo.shared.generated.resources.plus_assistance_subtitle
import yadlo.shared.generated.resources.plus_partners_count
import yadlo.shared.generated.resources.plus_payment_no_cash
import yadlo.shared.generated.resources.plus_stands_count
import yadlo.shared.generated.resources.plus_story_since

fun PlusState.toUiModel(): PlusUiModel {
    val loaded =
        overview ?: return PlusUiModel(
            isLoading = true,
            groups = emptyList(),
            socials = emptyList(),
        )

    val onSite =
        listOfNotNull(
            // Every row here is conditional on the section behind it, so the tab can never open a
            // screen with nothing on it — which is what lets the whole of Plus ship while half the
            // festival's practical information is still unpublished.
            // Two rows rather than one list with two headers: nobody looking for dinner is also
            // browsing for a second-hand costume. The Wishlist still groups them, because there
            // they are what one person kept.
            PlusRowUiModel(
                entry = PlusEntryUiModel.STANDS_FOOD,
                subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(loaded.foodStandCount)),
            ).takeIf { loaded.foodStandCount > 0 },
            PlusRowUiModel(
                entry = PlusEntryUiModel.STANDS_MAKERS,
                subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(loaded.makerStandCount)),
            ).takeIf { loaded.makerStandCount > 0 },
            PlusRowUiModel(
                entry = PlusEntryUiModel.PAYMENT,
                // Written on the row only when it is refused. That the site takes cards is not news;
                // that it takes nothing else is the single most consequential fact it publishes, and
                // it is only actionable before leaving the house.
                subtitle = UiText.Resource(Res.string.plus_payment_no_cash).takeIf { loaded.cashAccepted == false },
            ).takeIf { loaded.cashAccepted != null },
            PlusRowUiModel(entry = PlusEntryUiModel.ACCESS, subtitle = null).takeIf { loaded.hasTransport },
            PlusRowUiModel(entry = PlusEntryUiModel.HOURS, subtitle = null).takeIf { loaded.hasOpeningHours },
            PlusRowUiModel(
                entry = PlusEntryUiModel.ASSISTANCE,
                // The merge, said out loud. Three subjects behind one row needs the row to name them,
                // or nobody opens it until they are already looking for one of the three.
                subtitle = UiText.Resource(Res.string.plus_assistance_subtitle),
            ).takeIf { loaded.hasAssistance },
            PlusRowUiModel(entry = PlusEntryUiModel.FAQ, subtitle = null).takeIf { loaded.faqCount > 0 },
        )

    val festival =
        listOfNotNull(
            PlusRowUiModel(
                entry = PlusEntryUiModel.STORY,
                // "Depuis 2015" is the whole of what the row can promise, and it is the reason
                // someone taps it rather than the title.
                subtitle = UiText.Resource(Res.string.plus_story_since, listOf(loaded.foundedYear.toString())),
            ).takeIf { loaded.foundedYear != null },
            PlusRowUiModel(
                entry = PlusEntryUiModel.RESPONSIBLE,
                // The charters name themselves — "Charte FestiPlus" says more than "Festival
                // responsable" does, and it comes out of the content rather than out of a string.
                subtitle = UiText.Raw(loaded.charterNames.joinToString(" · ")),
            ).takeIf { loaded.charterNames.isNotEmpty() },
            PlusRowUiModel(
                entry = PlusEntryUiModel.PARTNERS,
                subtitle = UiText.Resource(Res.string.plus_partners_count, listOf(loaded.partnerCount)),
            ).takeIf { loaded.partnerCount > 0 },
        )

    val involvement =
        listOfNotNull(
            // Recruiting first, and above writing in. It is the one thing on this tab the
            // association is actively asking for, and it spent this feature buried inside a mail
            // router where only somebody already composing an email would have found it.
            PlusRowUiModel(entry = PlusEntryUiModel.VOLUNTEERING, subtitle = null).takeIf { loaded.hasVolunteering },
            PlusRowUiModel(entry = PlusEntryUiModel.CONTACT, subtitle = null).takeIf { loaded.hasContact },
            PlusRowUiModel(entry = PlusEntryUiModel.NEWSLETTER, subtitle = null).takeIf { loaded.newsletterUrl != null },
        )

    val app =
        listOfNotNull(
            // First in the group, and unconditional: it is the only row here that changes what the
            // app does rather than describing it, and the only one somebody arrives at Plus looking
            // for. Nothing gates it — a visitor who has never been asked for the permission still
            // has an answer to give.
            PlusRowUiModel(entry = PlusEntryUiModel.NOTIFICATIONS, subtitle = null),
            // The one row that is about the app rather than the festival, and the only one that
            // needs no content at all: it is what a committee member opens to find out who built
            // this and how to reach them.
            PlusRowUiModel(
                entry = PlusEntryUiModel.ABOUT,
                subtitle = UiText.Resource(Res.string.plus_about_unofficial),
            ),
            PlusRowUiModel(entry = PlusEntryUiModel.REPORT, subtitle = null).takeIf { loaded.reportEmail != null },
            PlusRowUiModel(entry = PlusEntryUiModel.PRIVACY, subtitle = null),
            // Unconditional, like the two app-owned rows above it: what it removes is what this
            // phone is holding, which exists whether or not the association has published anything.
            PlusRowUiModel(entry = PlusEntryUiModel.CLEAR_DATA, subtitle = null),
        )

    return PlusUiModel(
        isLoading = false,
        groups =
            listOf(
                PlusGroupUiModel(id = PlusGroupIdUiModel.ON_SITE, rows = onSite),
                PlusGroupUiModel(id = PlusGroupIdUiModel.FESTIVAL, rows = festival),
                PlusGroupUiModel(id = PlusGroupIdUiModel.INVOLVEMENT, rows = involvement),
                PlusGroupUiModel(id = PlusGroupIdUiModel.APP, rows = app),
            ).filter { it.rows.isNotEmpty() },
        // Not conditional on anything: an empty list draws no footer, which is the same answer the
        // row it replaced gave by not existing.
        socials =
            loaded.socials.map {
                SocialLinkUiModel(
                    // Raw, not a resource: the association's networks are named by the content,
                    // and a brand name does not translate.
                    id = it.id,
                    name = UiText.Raw(it.name),
                    icon = socialIconFor(it.id),
                    url = it.url,
                )
            },
    )
}
