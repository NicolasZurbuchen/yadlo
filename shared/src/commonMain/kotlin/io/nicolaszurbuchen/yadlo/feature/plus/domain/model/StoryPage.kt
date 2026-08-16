package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure

/**
 * *L'histoire de Yadlo* — where it came from, what a day there is like, and what the last edition
 * came to.
 *
 * **The figures live here rather than in an entry of their own.** Three numbers do not carry a row
 * on the tab, and out of context they are trivia; under the story of a festival started by
 * windsurfers in 2015 they are the point. Which is also why they come off the Edition while
 * everything above them comes off the live-truth file: browsing 2019 should show 2019's numbers and
 * the same origin.
 */
data class StoryPage(
    val foundedYear: Int,
    val body: String,
    val passageTitle: String?,
    val passageBody: String?,
    val figures: List<Figure>,
    /** False when any figure is from a past edition — the caveat Provenance owes the reader. */
    val figuresAreConfirmed: Boolean,
)
