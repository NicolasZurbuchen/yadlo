package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import io.nicolaszurbuchen.yadlo.infra.text.UiText

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
 * [logoUrl] is set for all thirty-nine partners the 2026 content declares. It stays nullable because
 * a partner is added before its logo arrives at least as often as the other way round, and because
 * the card draws the same fallback for a null url as for a load that failed: the name, which is what
 * the logo was standing for.
 */
data class PartnerUiModel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val url: String?,
)
