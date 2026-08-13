package io.nicolaszurbuchen.yadlo.common.content.domain.model

data class PartnerTier(
    val id: String,
    val name: String,
    val order: Int,
    val members: List<Partner>,
    val provenance: Provenance,
)
