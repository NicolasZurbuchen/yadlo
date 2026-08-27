package io.nicolaszurbuchen.yadlo.core.content.presentation.mapper

import io.nicolaszurbuchen.yadlo.core.content.domain.model.DietaryCoverage
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryTagUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.marksInOrder

/**
 * What a whole Stand can feed you, and how much of it.
 *
 * *Végétarien* is dropped when *Végan* covers exactly as much of the menu — but only then. A truck
 * with one vegan dish and an otherwise meat-free carte is *options véganes* **and**
 * *100 % végétarien*, and those are two different answers to two different people. The dish-level
 * twin in `design/` drops it unconditionally, because a dish is one thing and cannot be partly
 * anything.
 *
 * **It sits here rather than beside its twin because it names [DietaryCoverage].** The type and the
 * dish-level overload are the design system's — a tag is a mark and a label, and it would draw the
 * same in any app. *How much of a Stand's carte a mark covers* is this festival's content model,
 * and a file that names it belongs on the `core/` side of the line, in the one package inside
 * `presentation/` allowed to cross it.
 */
fun Map<String, DietaryCoverage>.toDietaryTags(): List<YadloDietaryTagUiModel> =
    keys
        .toList()
        .marksInOrder()
        .filterNot { mark ->
            mark == YadloDietaryMarkUiModel.VEGETARIAN && this[YadloDietaryMarkUiModel.VEGAN.id] == this[mark.id]
        }.map { mark ->
            YadloDietaryTagUiModel(
                mark = mark,
                label = if (getValue(mark.id) == DietaryCoverage.ALL) mark.allLabel else mark.someLabel,
            )
        }
