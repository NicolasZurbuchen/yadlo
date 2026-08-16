package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContactDto(
    val address: AddressDto,
    val phone: String? = null,
    val emails: List<EmailDto> = emptyList(),
    val provenance: String,
) {
    /**
     * Nested one level deeper than the model, which flattens it to a list of lines: the address is
     * printed as written and carries its own provenance in the file, but nothing reads the second
     * field and a wrapper that exists to hold one list is not worth a domain type.
     */
    @Serializable
    data class AddressDto(
        val lines: List<String> = emptyList(),
        val provenance: String,
    )

    @Serializable
    data class EmailDto(
        val id: String,
        val address: String,
        val label: String,
        val responsible: String? = null,
    )
}
