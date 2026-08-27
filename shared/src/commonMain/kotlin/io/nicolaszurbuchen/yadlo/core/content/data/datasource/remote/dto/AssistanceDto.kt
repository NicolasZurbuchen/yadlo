package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** `besoin`. */
@Serializable
data class AssistanceDto(
    val emergencyNumbers: List<EmergencyNumberDto> = emptyList(),
    val recognition: List<RecognitionDto> = emptyList(),
    val lostPropertyEmailId: String,
    val provenance: String,
) {
    @Serializable
    data class EmergencyNumberDto(
        val id: String,
        val label: String,
        val number: String,
    )

    @Serializable
    data class RecognitionDto(
        val id: String,
        val text: String,
    )
}
