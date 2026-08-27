package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `histoire` — the field names are the association's French, the model's are not. */
@Serializable
data class StoryDto(
    val foundedYear: Int,
    val body: String,
    @SerialName("journee")
    val passage: PassageDto? = null,
    val provenance: String,
) {
    @Serializable
    data class PassageDto(
        val title: String,
        val body: String,
        val provenance: String,
    )
}
