package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FestivalDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival

/**
 * No references to resolve and no instants to parse — `festival.json` is flat by the time it
 * reaches here. [FestivalDto.schemaVersion] is dropped rather than mapped: whether this build may
 * read the file at all is decided before mapping, not carried into the domain.
 */
fun FestivalDto.toDomain(): Festival =
    Festival(
        name = name,
        tagline = tagline,
        currentEditionId = currentEditionId,
        minSupportedAppVersion = minSupportedAppVersion,
    )
