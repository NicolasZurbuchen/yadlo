package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** `paiement`. */
@Serializable
data class PaymentDto(
    val methods: List<MethodDto> = emptyList(),
    val notes: List<NoteDto> = emptyList(),
    val links: List<InfoLinkDto> = emptyList(),
    val provenance: String,
) {
    @Serializable
    data class MethodDto(
        val id: String,
        val name: String,
        val accepted: Boolean,
    )

    @Serializable
    data class NoteDto(
        val id: String,
        val body: String,
    )
}
