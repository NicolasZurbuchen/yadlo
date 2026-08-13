package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One of the association's networks. [name] is carried rather than derived from [id], because a
 * network the app has never heard of still has to render with the name the organisers gave it —
 * the same reason [Link.type] stays a String.
 */
data class SocialLink(
    val id: String,
    val name: String,
    val url: String,
)
