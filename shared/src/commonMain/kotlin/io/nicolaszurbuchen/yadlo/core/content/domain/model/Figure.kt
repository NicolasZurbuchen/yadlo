package io.nicolaszurbuchen.yadlo.core.content.domain.model

/** [value] is a String because some figures are ranges, and it is only ever printed beside [label]. */
data class Figure(
    val id: String,
    val value: String,
    val label: String,
    val provenance: Provenance,
)
