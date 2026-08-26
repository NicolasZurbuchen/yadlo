package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.mapper

import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningKind
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningKindUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class HappeningKindUiMapperTest {
    @Test
    fun toUiModel_mapsEveryKindToItsOwnTwin() {
        // A table rather than three asserts: the interesting failure is two kinds landing on one
        // twin, which would send an Activity out under a concert's share sentence.
        val mapped = HappeningKind.entries.associateWith { it.toUiModel() }

        assertEquals(
            mapOf(
                HappeningKind.ARTIST to HappeningKindUiModel.ARTIST,
                HappeningKind.ACTIVITY to HappeningKindUiModel.ACTIVITY,
                HappeningKind.STAND to HappeningKindUiModel.STAND,
            ),
            mapped,
        )
    }

    @Test
    fun toUiModel_coversTheWholeDomainEnumWithNothingLeftOver() {
        assertEquals(HappeningKindUiModel.entries.size, HappeningKind.entries.size)
        assertEquals(HappeningKindUiModel.entries.toSet(), HappeningKind.entries.map { it.toUiModel() }.toSet())
    }
}
