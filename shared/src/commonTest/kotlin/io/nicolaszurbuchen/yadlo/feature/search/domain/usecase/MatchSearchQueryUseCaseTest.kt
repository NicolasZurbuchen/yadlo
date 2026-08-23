package io.nicolaszurbuchen.yadlo.feature.search.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchIndex
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MatchSearchQueryUseCaseTest {
    private val match = MatchSearchQueryUseCase()

    // region what a query reaches

    @Test
    fun invoke_aName_isFound() {
        val results = match(index(happenings = listOf(artist("dj-alf", name = "DJ ALF"))), "alf")

        assertEquals(listOf("dj-alf"), results.programme.map { it.happening.id })
    }

    @Test
    fun invoke_aNameMatch_carriesNoReason() {
        // The title is already the answer; a line under it repeating the query would be noise.
        val results = match(index(happenings = listOf(artist("dj-alf", name = "DJ ALF"))), "alf")

        assertNull(results.programme.single().reason)
    }

    @Test
    fun invoke_aGenre_isFoundAndSaysSo() {
        val dubside = artist("dubside", name = "Dubside", genres = listOf("Afro house"))

        val results = match(index(happenings = listOf(dubside)), "afro")

        assertEquals("Afro house", results.programme.single().reason)
    }

    @Test
    fun invoke_aDish_resolvesToTheStandThatSellsIt() {
        // A dish has no screen of its own, so the result is the thing the reader can actually open —
        // the same call story 8 makes for a Slot.
        val vegemania = stand("vegemania", name = "Vegemania", dishes = listOf("Ragoût de tofu" to null))

        val results = match(index(happenings = listOf(vegemania)), "tofu")

        assertEquals(listOf("vegemania"), results.onSite.map { it.happening.id })
        assertEquals("Ragoût de tofu", results.onSite.single().reason)
    }

    @Test
    fun invoke_aDishDescription_stillNamesTheDishRatherThanItsSentence() {
        val liban = stand("liban", dishes = listOf("Assiette de mezzés" to "Houmous, falafels et pain plat."))

        val results = match(index(happenings = listOf(liban)), "falafel")

        assertEquals("Assiette de mezzés", results.onSite.single().reason)
    }

    @Test
    fun invoke_theCuisine_isFound() {
        val results = match(index(happenings = listOf(stand("chez-nino", offering = "Cuisine libanaise"))), "libanaise")

        assertEquals("Cuisine libanaise", results.onSite.single().reason)
    }

    @Test
    fun invoke_theSuitabilityLine_isFound() {
        val coin = activity("coin-enfants", suitability = "Dès 4 ans, accompagné d'un adulte")

        val results = match(index(happenings = listOf(coin)), "4 ans")

        assertEquals("Dès 4 ans, accompagné d'un adulte", results.programme.single().reason)
    }

    @Test
    fun invoke_theDescription_isTheWidestNetAndComesLast() {
        val alf = artist("dj-alf", name = "DJ ALF", genres = listOf("House"), description = "Résident du MAD Club.")

        val results = match(index(happenings = listOf(alf)), "mad")

        assertEquals("Résident du MAD Club.", results.programme.single().reason)
    }

    @Test
    fun invoke_aQueryTypedWithoutAccents_stillFindsTheAccentedName() {
        val results = match(index(happenings = listOf(stand("vegemania", name = "Végémania"))), "vegemania")

        assertEquals(listOf("vegemania"), results.onSite.map { it.happening.id })
    }

    @Test
    fun invoke_nothingMatches_isEmptyRatherThanEverything() {
        val results = match(index(happenings = listOf(artist("dj-alf", name = "DJ ALF"))), "raclette")

        assertTrue(results.isEmpty)
    }

    // endregion

    // region the two halves

    @Test
    fun invoke_standsAndProgrammedThings_landInDifferentGroups() {
        // The line the domain already draws: an Activity has hours the organisers set, a Stand is
        // simply there while the site is open.
        val index =
            index(
                happenings =
                    listOf(
                        artist("sup-artist", name = "SUP Collective"),
                        activity("sup-yoga", name = "SUP Yoga"),
                        stand("sup-bar", name = "SUP Bar"),
                    ),
            )

        val results = match(index, "sup")

        assertEquals(listOf("sup-artist", "sup-yoga"), results.programme.map { it.happening.id })
        assertEquals(listOf("sup-bar"), results.onSite.map { it.happening.id })
    }

    // endregion

    // region ranking

    @Test
    fun invoke_aNameMatch_outranksAMatchOnAnythingElse() {
        // A reader who types "sup" and sees SUP Yoga under an activity whose description mentions it
        // would reasonably conclude the app did not understand them.
        val index =
            index(
                happenings =
                    listOf(
                        activity("apero", name = "Apéro", description = "Avec supplément."),
                        activity("sup-yoga", name = "SUP Yoga"),
                    ),
            )

        val results = match(index, "sup")

        assertEquals(listOf("sup-yoga", "apero"), results.programme.map { it.happening.id })
    }

    @Test
    fun invoke_withinTheSameKindOfMatch_ordersAlphabeticallyRatherThanByContentOrder() {
        val index =
            index(happenings = listOf(activity("zumba", name = "Zumba"), activity("aquagym", name = "Aquagym")))

        val results = match(index, "a")

        assertEquals(listOf("aquagym", "zumba"), results.programme.map { it.happening.id })
    }

    // endregion

    // region practical answers

    @Test
    fun invoke_anAliasNobodyWouldSeeOnScreen_reachesItsScreen() {
        // Story 9 by name: typing "twint" gets an answer.
        val results = match(index(topics = listOf(SearchTopic.PAYMENT, SearchTopic.HOURS)), "twint")

        assertEquals(listOf(SearchTopic.PAYMENT), results.topics)
    }

    @Test
    fun invoke_theOtherStory9Word_reachesTheTransportScreen() {
        val results = match(index(topics = listOf(SearchTopic.ACCESS, SearchTopic.PAYMENT)), "parking")

        assertEquals(listOf(SearchTopic.ACCESS), results.topics)
    }

    @Test
    fun invoke_aTopicTheEditionDoesNotPublish_cannotBeReachedAtAll() {
        // The index is what gates availability; a query cannot conjure a screen back.
        val results = match(index(topics = emptyList()), "twint")

        assertTrue(results.topics.isEmpty())
    }

    @Test
    fun invoke_aQuestion_isFoundByItsWordsAndByItsAnswer() {
        val index =
            index(
                faq =
                    listOf(
                        question("entree", text = "L'entrée est-elle payante ?", answer = "Non, elle est gratuite."),
                        question("chiens", text = "Les chiens sont-ils admis ?"),
                    ),
            )

        assertEquals(listOf("entree"), match(index, "payante").faq.map { it.id })
        assertEquals(listOf("entree"), match(index, "gratuite").faq.map { it.id })
    }

    // endregion

    // region the empty query

    @Test
    fun invoke_anEmptyQuery_findsNothingRatherThanEverything() {
        val index = index(happenings = listOf(artist("dj-alf")), topics = SearchTopic.entries)

        assertTrue(match(index, "").isEmpty)
    }

    @Test
    fun invoke_aQueryOfOnlySpaces_isTreatedAsEmpty() {
        val index = index(happenings = listOf(artist("dj-alf")), topics = SearchTopic.entries)

        assertTrue(match(index, "   ").isEmpty)
    }

    @Test
    fun invoke_surroundingSpaces_areIgnoredRatherThanSearchedFor() {
        val results = match(index(happenings = listOf(artist("dj-alf", name = "DJ ALF"))), "  alf  ")

        assertEquals(listOf("dj-alf"), results.programme.map { it.happening.id })
    }

    // endregion

    private fun index(
        happenings: List<Happening> = emptyList(),
        topics: List<SearchTopic> = emptyList(),
        faq: List<FaqEntry> = emptyList(),
    ) = SearchIndex(happenings = happenings, topics = topics, faq = faq)
}
