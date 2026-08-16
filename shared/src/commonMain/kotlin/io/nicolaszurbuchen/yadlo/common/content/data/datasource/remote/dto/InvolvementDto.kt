package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `simpliquer`. The two offers have genuinely different shapes rather than one shape with holes:
 * only volunteering has perks and a signup site, and only it needs them.
 */
@Serializable
data class InvolvementDto(
    @SerialName("hotstaff")
    val volunteering: VolunteeringDto? = null,
    @SerialName("partenaire")
    val partnership: PartnershipDto? = null,
) {
    @Serializable
    data class VolunteeringDto(
        val name: String,
        val body: String,
        val perks: List<String> = emptyList(),
        val signupUrl: String? = null,
        val contactEmailId: String,
        val provenance: String,
    )

    @Serializable
    data class PartnershipDto(
        val name: String,
        val body: String? = null,
        val contactEmailId: String,
        val provenance: String,
    )
}
