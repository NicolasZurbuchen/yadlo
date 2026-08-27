package io.nicolaszurbuchen.yadlo.core.content.domain.model

data class PartnerTier(
    val id: String,
    val name: String,
    val order: Int,
    val members: List<Partner>,
    val provenance: Provenance,
)
