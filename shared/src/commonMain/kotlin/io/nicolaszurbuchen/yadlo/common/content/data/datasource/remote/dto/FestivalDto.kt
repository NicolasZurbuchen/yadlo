package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `festival.json`, whole. The section names are the association's French and stay that way on the
 * wire; the `@SerialName` pairs are the only place the two vocabularies meet.
 *
 * **Every section defaults.** The published file always carries them — `validate.js` errors on an
 * omitted key — but a default here is what decides whether a file the app has not seen before costs
 * the visitor one screen or the whole festival. `ignoreUnknownKeys` covers the same drift in the
 * other direction, for content published ahead of the app that reads it.
 */
@Serializable
data class FestivalDto(
    val schemaVersion: Int,
    val name: String,
    val tagline: String,
    val currentEditionId: String,
    val minSupportedAppVersion: String? = null,
    val social: List<SocialDto> = emptyList(),
    val links: List<InfoLinkDto> = emptyList(),
    @SerialName("histoire")
    val story: StoryDto? = null,
    val faq: List<FaqEntryDto> = emptyList(),
    @SerialName("responsable")
    val responsible: ResponsibleDto? = null,
    val contact: ContactDto? = null,
    @SerialName("transports")
    val transport: TransportDto? = null,
    @SerialName("paiement")
    val payment: PaymentDto? = null,
    @SerialName("besoin")
    val assistance: AssistanceDto? = null,
    @SerialName("simpliquer")
    val involvement: InvolvementDto? = null,
) {
    @Serializable
    data class SocialDto(
        val id: String,
        val name: String,
        val url: String,
    )

    /** A wrapper around one list. Flattened on the way into the domain rather than carried. */
    @Serializable
    data class ResponsibleDto(
        val charters: List<CharterDto> = emptyList(),
    )
}
