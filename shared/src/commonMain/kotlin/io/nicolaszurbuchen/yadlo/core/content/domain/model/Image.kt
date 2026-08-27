package io.nicolaszurbuchen.yadlo.core.content.domain.model

/** [credit] exists because press photos usually carry a photographer's condition. */
data class Image(
    val url: String,
    val credit: String?,
)
