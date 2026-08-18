package io.nicolaszurbuchen.yadlo.app.design.uimodel

import io.nicolaszurbuchen.yadlo.common.content.domain.model.DietaryCoverage
import org.jetbrains.compose.resources.StringResource

/**
 * One dietary tag as it is drawn — the mark, and which of its three wordings applies here.
 *
 * The wording is resolved by whoever builds the tag rather than by the row that draws it, because
 * the same mark says three different things depending on what it is attached to: *Végan* on a dish,
 * *100 % végan* on a truck that sells nothing else, *Options véganes* on one that sells some.
 */
data class DietaryTagUiModel(
    val mark: DietaryMarkUiModel,
    val label: StringResource,
)

/**
 * What one dish is.
 *
 * *Végétarien* is dropped when *Végan* is there: both are true and every vegan dish carries both,
 * but a dish reading *Végan · Végétarien · Sans lactose* spends three tags saying roughly one thing,
 * on a row that already carries a name, a price and a line of ingredients. Only the drawing drops
 * it — the filter still matches on everything the dish carries.
 */
fun List<String>.toDietaryTags(): List<DietaryTagUiModel> {
    val marks = marksInOrder()

    return marks
        .filterNot { it == DietaryMarkUiModel.VEGETARIAN && DietaryMarkUiModel.VEGAN in marks }
        .map { DietaryTagUiModel(mark = it, label = it.label) }
}

/**
 * What a whole Stand can feed you, and how much of it.
 *
 * *Végétarien* is dropped the same way, but only when *Végan* covers exactly as much of the menu.
 * A truck with one vegan dish and an otherwise meat-free carte is *options véganes* **and**
 * *100 % végétarien*, and those are two different answers to two different people.
 */
fun Map<String, DietaryCoverage>.toDietaryTags(): List<DietaryTagUiModel> =
    keys
        .toList()
        .marksInOrder()
        .filterNot { mark ->
            mark == DietaryMarkUiModel.VEGETARIAN && this[DietaryMarkUiModel.VEGAN.id] == this[mark.id]
        }.map { mark ->
            DietaryTagUiModel(
                mark = mark,
                label = if (getValue(mark.id) == DietaryCoverage.ALL) mark.allLabel else mark.someLabel,
            )
        }

/**
 * Resolved and ordered by the enum rather than by the content, so the same two marks are never in
 * one order on a stand row and the other order on the dish it opens. An id this build has no answer
 * for simply drops — see [DietaryMarkUiModel.forId].
 */
private fun List<String>.marksInOrder(): List<DietaryMarkUiModel> {
    val marks = mapNotNull(DietaryMarkUiModel::forId).toSet()

    return DietaryMarkUiModel.entries.filter { it in marks }
}
