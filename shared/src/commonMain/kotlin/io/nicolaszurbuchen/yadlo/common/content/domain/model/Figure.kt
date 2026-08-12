package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One of an Edition's closing statistics — "6000 visiteurs", "3200 litres de biere".
 *
 * [value] is a String rather than a number because the content writes some of these as ranges or
 * with a qualifier, and the screen only ever prints it next to [label].
 */
data class Figure(
    val id: String,
    val value: String,
    val label: String,
    val provenance: Provenance,
)
