package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A photograph attached to a Happening.
 *
 * [credit] exists because press photos usually carry a photographer's condition, and dropping it
 * is the kind of thing that turns a free asset into a complaint.
 */
data class Image(
    val url: String,
    val credit: String?,
)
