package io.nicolaszurbuchen.yadlo.common.content.domain.model

/** A named level of sponsorship, holding the partners at that level in display order. */
data class PartnerTier(
    val id: String,
    val name: String,
    val order: Int,
    val members: List<Partner>,
    val provenance: Provenance,
)
