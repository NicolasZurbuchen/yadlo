package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.mapper

import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchTopicUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchTopicUiMapperTest {
    @Test
    fun toUiModel_mapsEveryTopicToTheTwinOfTheSameName() {
        // Fifteen pairs written out by hand in the mapper, which is exactly the length at which a
        // copied line with one name left unchanged stops being visible in review. Two topics
        // landing on one twin would send *Paiement* to the wrong screen and say nothing about it.
        assertEquals(
            SearchTopic.entries.associateWith { SearchTopicUiModel.valueOf(it.name) },
            SearchTopic.entries.associateWith { it.toUiModel() },
        )
    }

    @Test
    fun toUiModel_coversTheWholeDomainEnumWithNothingLeftOver() {
        // The two enums are a pair, and a sixteenth topic added without a twin would otherwise
        // surface as a compile error in one file and nowhere else.
        assertEquals(SearchTopicUiModel.entries.size, SearchTopic.entries.size)
        assertEquals(SearchTopicUiModel.entries.toSet(), SearchTopic.entries.map { it.toUiModel() }.toSet())
    }
}
