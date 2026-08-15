package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Partenaires* — the companies without which there is no festival, in their tiers.
 *
 * **This is the one screen in the app that is drawn for someone other than the visitor.** These
 * logos belong to businesses that paid to be seen, and the tier order is the hierarchy they paid
 * into, which is why nothing here is re-sorted or flattened for tidiness.
 */
data class PartnersUiModel(
    val isLoading: Boolean,
    val tiers: List<PartnerTierUiModel>,
    val emptyMessage: UiText?,
    /** Null until a partner with no website has been tapped. Its token changes on every tap. */
    val noWebsiteNotice: PartnersNoticeUiModel?,
)

/**
 * [token] is what makes the second tap say it again. The screen keys its snackbar on the number, so
 * a repeated tap is a new value and re-entering the screen is not.
 */
data class PartnersNoticeUiModel(
    val token: Int,
    val message: UiText,
)

data class PartnerTierUiModel(
    val id: String,
    val name: String,
    val members: List<PartnerUiModel>,
)

/**
 * [logoUrl] is null for every partner in the 2026 content: the field exists and no file has been
 * supplied, so the name is drawn instead. That is the right fallback rather than a placeholder box
 * — a company's name is the thing the logo was standing for.
 */
data class PartnerUiModel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val url: String?,
)
