package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

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
        )

    val onSite =
        listOfNotNull(
            // Every row here is conditional on the section behind it, so the tab can never open a
            // screen with nothing on it — which is what lets the whole of Plus ship while half the
            // festival's practical information is still unpublished.
            PlusRowUiModel(
                entry = PlusEntry.STANDS,
                subtitle = UiText.Resource(Res.string.plus_stands_count, listOf(loaded.standCount)),
            ).takeIf { loaded.standCount > 0 },
            PlusRowUiModel(
                entry = PlusEntry.PAYMENT,
                // Written on the row only when it is refused. That the site takes cards is not news;
                // that it takes nothing else is the single most consequential fact it publishes, and
                // it is only actionable before leaving the house.
                subtitle = UiText.Resource(Res.string.plus_payment_no_cash).takeIf { loaded.cashAccepted == false },
            ).takeIf { loaded.cashAccepted != null },
            PlusRowUiModel(entry = PlusEntry.ACCESS, subtitle = null).takeIf { loaded.hasTransport },
            PlusRowUiModel(entry = PlusEntry.ACCESSIBILITY, subtitle = null).takeIf { loaded.hasAccessibility },
            PlusRowUiModel(entry = PlusEntry.HOURS, subtitle = null).takeIf { loaded.hasOpeningHours },
            PlusRowUiModel(
                entry = PlusEntry.ASSISTANCE,
                // The merge, said out loud. Three subjects behind one row needs the row to name them,
                // or nobody opens it until they are already looking for one of the three.
                subtitle = UiText.Resource(Res.string.plus_assistance_subtitle),
            ).takeIf { loaded.hasAssistance },
            PlusRowUiModel(entry = PlusEntry.FAQ, subtitle = null).takeIf { loaded.faqCount > 0 },
        )

    val festival =
        listOfNotNull(
            PlusRowUiModel(
                entry = PlusEntry.STORY,
                // "Depuis 2015" is the whole of what the row can promise, and it is the reason
                // someone taps it rather than the title.
                subtitle = UiText.Resource(Res.string.plus_story_since, listOf(loaded.foundedYear.toString())),
            ).takeIf { loaded.foundedYear != null },
            PlusRowUiModel(
                entry = PlusEntry.RESPONSIBLE,
                // The charters name themselves — "Charte FestiPlus" says more than "Festival
                // responsable" does, and it comes out of the content rather than out of a string.
                subtitle = UiText.Raw(loaded.charterNames.joinToString(" · ")),
            ).takeIf { loaded.charterNames.isNotEmpty() },
            PlusRowUiModel(
                entry = PlusEntry.PARTNERS,
                subtitle = UiText.Resource(Res.string.plus_partners_count, listOf(loaded.partnerCount)),
            ).takeIf { loaded.partnerCount > 0 },
        )

    val involvement =
        listOfNotNull(
            PlusRowUiModel(entry = PlusEntry.CONTACT, subtitle = null).takeIf { loaded.hasContact },
            PlusRowUiModel(entry = PlusEntry.NEWSLETTER, subtitle = null).takeIf { loaded.newsletterUrl != null },
            PlusRowUiModel(entry = PlusEntry.SOCIAL, subtitle = null).takeIf { loaded.socialCount > 0 },
        )

    val app =
        listOfNotNull(
            // The one row that is about the app rather than the festival, and the only one that
            // needs no content at all: it is what a committee member opens to find out who built
            // this and how to reach them.
            PlusRowUiModel(
                entry = PlusEntry.ABOUT,
                subtitle = UiText.Resource(Res.string.plus_about_unofficial),
            ),
            PlusRowUiModel(entry = PlusEntry.REPORT, subtitle = null).takeIf { loaded.reportEmail != null },
            PlusRowUiModel(entry = PlusEntry.PRIVACY, subtitle = null),
        )

    return PlusUiModel(
        isLoading = false,
        groups =
            listOf(
                PlusGroupUiModel(id = PlusGroupUiId.ON_SITE, rows = onSite),
                PlusGroupUiModel(id = PlusGroupUiId.FESTIVAL, rows = festival),
                PlusGroupUiModel(id = PlusGroupUiId.INVOLVEMENT, rows = involvement),
                PlusGroupUiModel(id = PlusGroupUiId.APP, rows = app),
            ).filter { it.rows.isNotEmpty() },
    )
}
