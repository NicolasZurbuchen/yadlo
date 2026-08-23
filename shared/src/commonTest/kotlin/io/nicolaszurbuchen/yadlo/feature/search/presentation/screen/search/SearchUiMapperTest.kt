package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchHit
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchResults
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.artist
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.question
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.stand
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.search_empty
import yadlo.shared.generated.resources.search_hint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchUiMapperTest {
    // region the two ways of having nothing

    @Test
    fun toUiModel_anEmptyField_invitesRatherThanReportingFailure() {
        // An empty field has not asked anything, so answering "aucun résultat" would be the screen
        // failing a question nobody put to it.
        val result = SearchState().toUiModel()

        assertEquals(UiText.Resource(Res.string.search_hint), result.message)
        assertTrue(result.groups.isEmpty())
    }

    @Test
    fun toUiModel_aQueryThatFoundNothing_saysSoAndQuotesIt() {
        val result = state(query = "raclette").toUiModel()

        assertEquals(UiText.Resource(Res.string.search_empty, listOf("raclette")), result.message)
    }

    @Test
    fun toUiModel_aQueryPaddedWithSpaces_isQuotedBackTrimmed() {
        val result = state(query = "  raclette  ").toUiModel()

        assertEquals(UiText.Resource(Res.string.search_empty, listOf("raclette")), result.message)
    }

    @Test
    fun toUiModel_aFieldOfOnlySpaces_stillReadsAsEmpty() {
        val result = state(query = "   ").toUiModel()

        assertEquals(UiText.Resource(Res.string.search_hint), result.message)
    }

    @Test
    fun toUiModel_somethingWasFound_hasNoMessageAtAll() {
        // Exactly one of a message and a list is ever on screen.
        val result = state(query = "alf", programme = listOf(hit(artist("dj-alf", name = "DJ ALF")))).toUiModel()

        assertNull(result.message)
    }

    // endregion

    // region groups

    @Test
    fun toUiModel_groups_runProgrammeThenSurPlaceThenInfosPratiques() {
        // The order of the questions, which is also the order of the tabs.
        val result =
            state(
                query = "sup",
                programme = listOf(hit(artist("dj-alf"))),
                onSite = listOf(hit(stand("vegemania"))),
                topics = listOf(SearchTopicUiModel.PAYMENT),
            ).toUiModel()

        assertEquals(listOf("programme", "on-site", "practical"), result.groups.map { it.id })
    }

    @Test
    fun toUiModel_aGroupWithNothingInIt_isAbsentRatherThanEmpty() {
        val result = state(query = "sup", onSite = listOf(hit(stand("vegemania")))).toUiModel()

        assertEquals(listOf("on-site"), result.groups.map { it.id })
    }

    @Test
    fun toUiModel_rowKeys_areUniqueAcrossTheWholeFlattenedList() {
        // The LazyColumn draws one list, so a key that only has to be unique within its group would
        // collide the moment the same Happening could appear twice.
        val result =
            state(
                query = "sup",
                programme = listOf(hit(artist("sup"))),
                onSite = listOf(hit(stand("sup"))),
                topics = listOf(SearchTopicUiModel.PAYMENT),
                faq = listOf(question("sup", "Un SUP ?")),
            ).toUiModel()

        val ids = result.groups.flatMap { group -> group.rows.map { it.id } }

        assertEquals(ids.size, ids.toSet().size)
    }

    // endregion

    // region rows

    @Test
    fun toUiModel_aHappeningRow_carriesItsCategoryInWordsAsWellAsInColour() {
        val result = state(query = "alf", programme = listOf(hit(artist("dj-alf", name = "DJ ALF")))).toUiModel()

        val row = assertIs<SearchRowUiModel.Happening>(result.groups.single().rows.single())

        assertEquals("DJ ALF", row.name)
        assertEquals("musique", row.categoryId)
        assertEquals("Musique", row.categoryName)
    }

    @Test
    fun toUiModel_aRowFoundByItsName_hasNoReasonToGive() {
        val result = state(query = "alf", programme = listOf(hit(artist("dj-alf", name = "DJ ALF")))).toUiModel()

        assertNull(assertIs<SearchRowUiModel.Happening>(result.groups.single().rows.single()).reason)
    }

    @Test
    fun toUiModel_aRowFoundByADish_saysWhichDish() {
        val hit = hit(stand("vegemania"), reason = "Ragoût de tofu")

        val result = state(query = "tofu", onSite = listOf(hit)).toUiModel()

        assertEquals("Ragoût de tofu", assertIs<SearchRowUiModel.Happening>(result.groups.single().rows.single()).reason)
    }

    @Test
    fun toUiModel_aHappeningRow_pointsAtTheFicheRatherThanAtItself() {
        val result = state(query = "alf", programme = listOf(hit(artist("dj-alf")))).toUiModel()

        assertEquals("dj-alf", assertIs<SearchRowUiModel.Happening>(result.groups.single().rows.single()).happeningId)
    }

    @Test
    fun toUiModel_aTopicRow_takesThePlusTabsOwnNameForTheScreen() {
        val result = state(query = "twint", topics = listOf(SearchTopicUiModel.PAYMENT)).toUiModel()

        val row = assertIs<SearchRowUiModel.Practical>(result.groups.single().rows.single())

        assertEquals(SearchTopicUiModel.PAYMENT, row.topic)
        assertEquals(UiText.Resource(SearchTopicUiModel.PAYMENT.title), row.title)
    }

    @Test
    fun toUiModel_aQuestion_isTitledInTheContentsOwnWordsAndOpensTheFaq() {
        // There is no screen for a single question, so a row that pretended otherwise would
        // dead-end — the same rule that resolves a dish to its stand.
        val result = state(query = "payante", faq = listOf(question("entree", "L'entrée est-elle payante ?"))).toUiModel()

        val row = assertIs<SearchRowUiModel.Practical>(result.groups.single().rows.single())

        assertEquals(UiText.Raw("L'entrée est-elle payante ?"), row.title)
        assertEquals(SearchTopicUiModel.FAQ, row.topic)
    }

    @Test
    fun toUiModel_topics_comeBeforeTheQuestionsInTheSameGroup() {
        // A topic is a whole screen and a question is one line on one of them.
        val result =
            state(
                query = "twint",
                topics = listOf(SearchTopicUiModel.PAYMENT),
                faq = listOf(question("entree", "L'entrée est-elle payante ?")),
            ).toUiModel()

        assertEquals(listOf("topic-PAYMENT", "faq-entree"), result.groups.single().rows.map { it.id })
    }

    // endregion

    @Test
    fun toUiModel_theQuery_isHandedBackUntouchedSoTheFieldKeepsWhatWasTyped() {
        val result = state(query = "  Ragoût  ").toUiModel()

        assertEquals("  Ragoût  ", result.query)
    }

    private fun hit(
        happening: Happening,
        reason: String? = null,
    ) = SearchHit(happening = happening, reason = reason)

    private fun state(
        query: String = "",
        programme: List<SearchHit> = emptyList(),
        onSite: List<SearchHit> = emptyList(),
        topics: List<SearchTopicUiModel> = emptyList(),
        faq: List<FaqEntry> = emptyList(),
    ) = SearchState(
        query = query,
        index = null,
        results =
            SearchResults(
                programme = programme,
                onSite = onSite,
                // The domain form of the topics; the mapper reads the converted list beside it.
                topics = topics.map { SearchTopic.valueOf(it.name) },
                faq = faq,
            ),
        topics = topics,
    )
}
