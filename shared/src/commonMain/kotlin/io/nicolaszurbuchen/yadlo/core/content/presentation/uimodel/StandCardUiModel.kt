package io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryTagUiModel

/**
 * One Stand as it is drawn wherever a Stand is listed — *Nourriture & boissons*, *Créateurs*, and
 * *À essayer*.
 *
 * It sits in `common/content` for the reason [SlotSegmentUiModel] does: three screens in two
 * features draw the same object, and three copies of the same four fields is three places for them
 * to drift apart. It moved up here for the third caller rather than in anticipation of it.
 *
 * [imageUrl] is what stopped the *Créateurs* list being two lines of grey text on a white page.
 * Every one of the eight Stands the 2026 edition declares has a photograph, and it is the only
 * thing on the card that says what the place actually looks like when you are standing in front of
 * forty stalls. Null is still handled — the bundled picture of the site goes behind instead, the
 * same fallback the fiche's head uses — because a Stand is normally added before its photograph
 * arrives.
 *
 * [offering] is the one line that answers what someone walking the row is asking: "Cuisine
 * géorgienne". [dietary] says what can be eaten there, derived from the menu, and whether the mark
 * covers all of it or part of it.
 *
 * **No hours.** Not one Stand publishes any — see content/GAPS.md — and a time here would be
 * invented.
 */
data class StandCardUiModel(
    val id: String,
    val name: String,
    val offering: String?,
    val imageUrl: String?,
    val dietary: List<YadloDietaryTagUiModel>,
)
