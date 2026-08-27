package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.mapper

import io.nicolaszurbuchen.yadlo.core.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel

/**
 * The half being shown, back in the app's own words.
 *
 * The trip is longer here than on other screens and each leg has a reason. The back stack carries
 * `StandsKindUiModel`, because a NavKey may not name a domain type; the Store converts it once at
 * construction, because `StandKind` is what the content is keyed by and what the State should hold;
 * and this converts it again for the bar, because the title is a string the content does not
 * supply — the Category is *Restauration*, and the entry somebody taps when they are hungry says
 * *Nourriture & boissons*.
 */
fun StandKind.toUiModel(): StandsKindUiModel =
    when (this) {
        StandKind.FOOD -> StandsKindUiModel.FOOD
        StandKind.MAKERS -> StandsKindUiModel.MAKERS
    }
