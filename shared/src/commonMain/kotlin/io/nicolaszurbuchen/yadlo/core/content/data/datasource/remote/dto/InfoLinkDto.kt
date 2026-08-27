package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * `festival.json` writes this shape in three places — the standing links, a transport mode's
 * timetables, the TWINT page — and one of them omits `sublabel` entirely, so it defaults rather
 * than being written null everywhere it is not used.
 */
@Serializable
data class InfoLinkDto(
    val id: String,
    val label: String,
    val sublabel: String? = null,
    val url: String,
)
