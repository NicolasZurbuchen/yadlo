package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.mapper

import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class StandsKindUiMapperTest {
    @Test
    fun toUiModel_mapsEveryKindToItsOwnTwin() {
        // Two entries, so the pairing is easy to get right and easy to get silently wrong: swapping
        // them puts *Créateurs* in the bar over a list of food trucks.
        val mapped = StandKind.entries.associateWith { it.toUiModel() }

        assertEquals(
            mapOf(
                StandKind.FOOD to StandsKindUiModel.FOOD,
                StandKind.MAKERS to StandsKindUiModel.MAKERS,
            ),
            mapped,
        )
    }

    @Test
    fun toUiModel_coversTheWholeDomainEnumWithNothingLeftOver() {
        assertEquals(StandsKindUiModel.entries.size, StandKind.entries.size)
        assertEquals(StandsKindUiModel.entries.toSet(), StandKind.entries.map { it.toUiModel() }.toSet())
    }
}
