package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * How the festival tells its own beginning — *L'histoire de Yadlo*, since 2015.
 *
 * Lives in the live-truth file rather than on an Edition, by the test that decides everything
 * there: a past-edition archive does not need its own copy of how the festival started, because the
 * answer is the same in every year's telling.
 */
data class Story(
    val foundedYear: Int,
    val body: String,
    /** *Une journée à Yadlo* — dawn to the last set. Null when only the origin has been written. */
    val passage: Passage?,
    val provenance: Provenance,
) {
    /**
     * A titled passage under the main body. Carries its own [Provenance] because the two are
     * sourced differently: the origin is the association's own words, the day is a retelling.
     */
    data class Passage(
        val title: String,
        val body: String,
        val provenance: Provenance,
    )
}
