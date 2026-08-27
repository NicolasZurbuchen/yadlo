package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFactMarkUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Accès* — every published way of getting to the beach, in the order the content declares.
 *
 * That order is chronological rather than alphabetical: coming first, going home after, the way the
 * page is read before leaving the house. **The cost is real and was accepted** — at two in the
 * morning the last bus takes a little scrolling to reach. Reordering by time of use would put the
 * night bus first for everyone all day, which is wrong more often than it is right.
 */
data class AccessUiModel(
    val isLoading: Boolean,
    val modes: List<AccessModeUiModel>,
    val emptyMessage: UiText?,
)

data class AccessModeUiModel(
    val id: String,
    val name: String,
    val body: String?,
    val facts: List<AccessFactUiModel>,
    val links: List<AccessLinkUiModel>,
    val nights: List<AccessNightUiModel>,
)

/**
 * One stated condition of arriving this way, marked ✓ or ⓘ.
 *
 * The mark is the whole point of the split: *deux places réservées près de l'entrée* and *places
 * limitées* are both true, and the reader deciding whether to drive needs to see at a glance which
 * of the two is the offer and which is the warning. In a paragraph they weigh the same.
 */
data class AccessFactUiModel(
    val id: String,
    val text: String,
    val mark: YadloFactMarkUiModel,
)

/**
 * One night of departures, not one row per bus. Seven buses read as two lines instead of filling
 * the screen, which is the whole reason the content groups them this way.
 *
 * [notes] keeps each remark attached to the time it is about — *03:00 — pas de correspondance pour
 * Lausanne* — because that one is the difference between getting home and sleeping at Morges, and a
 * footnote that has lost its time says nothing.
 */
data class AccessNightUiModel(
    val id: String,
    val night: String,
    val times: String,
    val notes: List<String>,
)

data class AccessLinkUiModel(
    val id: String,
    val label: String,
    val sublabel: String?,
    val url: String,
)
