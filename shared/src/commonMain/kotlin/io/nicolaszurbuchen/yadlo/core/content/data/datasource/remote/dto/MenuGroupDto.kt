package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MenuGroupDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val source: String? = null,
    val items: List<ItemDto> = emptyList(),
) {
    @Serializable
    data class ItemDto(
        val name: String,
        val price: MoneyDto? = null,
        val description: String? = null,
        val marks: List<String> = emptyList(),
        val provenance: String,
    )
}
