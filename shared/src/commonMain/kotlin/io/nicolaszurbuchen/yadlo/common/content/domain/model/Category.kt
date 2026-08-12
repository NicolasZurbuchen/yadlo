package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * What kind of thing a Happening is — the only grouping axis in the app.
 *
 * The label and the order live in the content so the Programme's filter chips get their French names
 * and their sequence from data rather than from a hardcoded list in Kotlin. The colour deliberately
 * does not: that is a design decision made once against a measured palette, and it lives in
 * CategoryColors in the theme.
 */
data class Category(
    val id: String,
    val name: String,
    val order: Int,
)
