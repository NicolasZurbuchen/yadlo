package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * An outbound link on a Happening.
 *
 * [type] stays a String rather than an enum: the content adds a platform whenever an artist has one
 * the others do not, and a new value should render with a generic icon rather than fail to parse the
 * whole edition. Ten types are in use today and none of them is load-bearing.
 */
data class Link(
    val type: String,
    val url: String,
)
