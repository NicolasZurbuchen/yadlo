package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.mapper

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.PhaseUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class PhaseUiMapperTest {
    @Test
    fun toUiModel_mapsEveryPhaseToItsOwnTwin() {
        // Written as a table rather than five asserts, because the interesting failure is not one
        // wrong pair — it is two Phases arriving at the same UiModel, which a table shows and five
        // asserts do not.
        val mapped = Phase.entries.associateWith { it.toUiModel() }

        assertEquals(
            mapOf(
                Phase.OFF_SEASON to PhaseUiModel.OFF_SEASON,
                Phase.ANNOUNCED to PhaseUiModel.ANNOUNCED,
                Phase.APPROACHING to PhaseUiModel.APPROACHING,
                Phase.LIVE to PhaseUiModel.LIVE,
                Phase.ENDED to PhaseUiModel.ENDED,
            ),
            mapped,
        )
    }

    @Test
    fun toUiModel_coversTheWholeDomainEnumWithNothingLeftOver() {
        // The two enums are a pair, and a sixth Phase added without a twin would otherwise surface
        // as a compile error in one file and nowhere else. This says the pairing is total.
        assertEquals(PhaseUiModel.entries.size, Phase.entries.size)
        assertEquals(PhaseUiModel.entries.toSet(), Phase.entries.map { it.toUiModel() }.toSet())
    }
}
