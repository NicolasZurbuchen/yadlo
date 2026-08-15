package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_assistance_subtitle
import yadlo.shared.generated.resources.plus_payment_no_cash
import yadlo.shared.generated.resources.plus_stands_count

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

    return PlusUiModel(
        isLoading = false,
        groups =
            listOf(PlusGroupUiModel(id = PlusGroupUiId.ON_SITE, rows = onSite))
                .filter { it.rows.isNotEmpty() },
    )
}
