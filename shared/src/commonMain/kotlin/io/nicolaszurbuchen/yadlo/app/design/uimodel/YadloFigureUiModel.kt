package io.nicolaszurbuchen.yadlo.app.design.uimodel

/**
 * One number out of *Yadlo en chiffres* — six thousand visitors, a hundred and sixty volunteers.
 *
 * In the design system because two screens print the same three numbers: Accueil closes the ENDED
 * phase with them, and *L'histoire de Yadlo* sets them under the story that gives them their point.
 * They were two identical data classes and two hand-laid grids that had already drifted on the one
 * thing anybody would notice — the colour of the number.
 *
 * [value] is a String because some figures are ranges, and it is only ever printed beside [label].
 */
data class FigureUiModel(
    val id: String,
    val value: String,
    val label: String,
)
