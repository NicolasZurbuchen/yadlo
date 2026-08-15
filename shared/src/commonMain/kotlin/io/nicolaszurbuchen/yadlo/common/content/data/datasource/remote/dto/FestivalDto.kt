package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * Only the fields the loading chain reads. The rest of `festival.json` parses and is discarded by
 * `ignoreUnknownKeys`, which is what lets the Plus sections be modelled when their screens exist
 * rather than kept warm in the meantime.
 */
@Serializable
data class FestivalDto(
    val schemaVersion: Int,
    val name: String,
    val tagline: String,
    val currentEditionId: String,
    val minSupportedAppVersion: String? = null,
    val social: List<SocialDto> = emptyList(),
) {
    @Serializable
    data class SocialDto(
        val id: String,
        val name: String,
        val url: String,
    )
}
