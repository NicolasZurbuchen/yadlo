package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** The content calls it [src]; the domain calls it `url`. The mapper does the renaming. */
@Serializable
data class ImageDto(
    val src: String,
    val credit: String? = null,
)
