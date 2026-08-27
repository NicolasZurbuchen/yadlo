package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * [type] stays a String rather than an enum: a new platform should render with a generic icon
 * rather than fail to parse the whole edition.
 */
data class Link(
    val type: String,
    val url: String,
)
