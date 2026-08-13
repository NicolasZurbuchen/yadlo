package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MenuGroupDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val source: String? = null,
    val items: List<Item> = emptyList(),
) {
    @Serializable
    data class Item(
        val name: String,
        val price: MoneyDto? = null,
        val description: String? = null,
        val marks: List<String> = emptyList(),
        val provenance: String,
    )
}
