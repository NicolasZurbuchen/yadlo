package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** `accessibilite`. `items` is published empty, which is the honest state rather than a gap. */
@Serializable
data class AccessibilityDto(
    val items: List<ItemDto> = emptyList(),
    val contactEmailId: String,
    val provenance: String,
) {
    @Serializable
    data class ItemDto(
        val id: String,
        val name: String,
        val available: Boolean,
        val note: String? = null,
    )
}
