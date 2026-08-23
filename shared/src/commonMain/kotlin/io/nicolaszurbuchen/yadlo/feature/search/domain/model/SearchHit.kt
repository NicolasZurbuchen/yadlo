package io.nicolaszurbuchen.yadlo.feature.search.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening

/**
 * A Happening the query reached, and the text that reached it.
 *
 * **The result is the Happening, never the dish or the Slot.** Story 8 settles this for the
 * timetable — an activity running three days is one result listing its dates rather than three
 * near-identical rows — and a dish is the same case one level down: *Ragoût de tofu* has no screen,
 * so a row offering it would either dead-end or open the stand under a name the reader did not tap.
 * Every result in the app resolves to something with a fiche.
 *
 * [reason] is what makes that honest. It carries the value that actually matched — the dish, the
 * genre, the cuisine — so a row whose title has nothing to do with the query still says why it is
 * there. It is null exactly when the name matched, because then the title is already the answer and
 * a second line repeating it would be noise.
 */
data class SearchHit(
    val happening: Happening,
    val reason: String?,
)
