package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * The only grouping axis in the app. Label and order come from the content so the Programme's
 * filter chips are not hardcoded French; the **colour does not** — that is a design decision made
 * once against a measured palette, and it lives in CategoryColors.
 */
data class Category(
    val id: String,
    val name: String,
    val order: Int,
)
