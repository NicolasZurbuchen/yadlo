package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import io.nicolaszurbuchen.yadlo.infra.text.UiText

/**
 * *Effacer mes données* — two things the app is holding, and a button against each.
 *
 * **Two rows rather than one *tout effacer*.** They are not the same kind of loss: the Plan is
 * something the visitor made and cannot get back, the pictures are something the network gave and
 * will give again. A single button would price the cheap one at the cost of the expensive one, and
 * anybody who only wanted the storage back would be asked to give up their weekend to get it.
 *
 * [isConfirmingSaved] is the question the destructive half asks first. The other half asks nothing,
 * which is the difference between the two written as behaviour rather than as a warning nobody
 * reads.
 */
data class ClearDataUiModel(
    val isLoading: Boolean,
    val saved: ClearDataRowUiModel,
    val images: ClearDataRowUiModel,
    val isConfirmingSaved: Boolean,
)

/**
 * One thing the app is holding: what it is, how much of it there is, and whether there is anything
 * to press.
 *
 * [detail] is always present — "rien d'enregistré" and "3 créneaux · 2 stands" are the same field,
 * because an empty state that appears by a line vanishing leaves the reader wondering whether the
 * screen finished loading. [isEnabled] is false in exactly that case: a button that would delete
 * nothing is a button that cannot report success.
 */
data class ClearDataRowUiModel(
    val title: UiText,
    val body: UiText,
    val detail: UiText,
    val action: UiText,
    val isEnabled: Boolean,
)
