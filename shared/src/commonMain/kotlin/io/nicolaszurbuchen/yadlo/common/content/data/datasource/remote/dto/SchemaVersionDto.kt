package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * Every content file carries `schemaVersion` at its root, and this reads only that. Deciding whether
 * a document may be cached must not require parsing the document — that is the whole point of the
 * check, since a file this build cannot read is exactly the file that would fail to parse.
 */
@Serializable
data class SchemaVersionDto(
    val schemaVersion: Int,
)
