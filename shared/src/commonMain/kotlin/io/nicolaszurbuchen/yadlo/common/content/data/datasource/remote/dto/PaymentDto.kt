package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** `paiement`. */
@Serializable
data class PaymentDto(
    val headline: String? = null,
    val summary: String? = null,
    val methods: List<MethodDto> = emptyList(),
    val notes: List<NoteDto> = emptyList(),
    val provenance: String,
) {
    @Serializable
    data class MethodDto(
        val id: String,
        val name: String,
        val accepted: Boolean,
    )

    /**
     * **[title] defaults like every other field here, and for the reason `FestivalDto` gives.** A
     * required field nested inside a section quietly cancels that file's promise: it is not the
     * section that fails to parse, it is `festival.json`, so one heading the published content has
     * not caught up with yet costs the visitor the whole festival rather than one screen. The app is
     * tolerant on the wire and `validate.js` is strict about what we publish.
     */
    @Serializable
    data class NoteDto(
        val id: String,
        val title: String? = null,
        val body: String,
        val links: List<InfoLinkDto> = emptyList(),
    )
}
