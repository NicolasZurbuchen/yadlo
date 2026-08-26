package io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation

import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel

/**
 * **A method per practical destination rather than a key**, exactly as `HomeNavigator` does for the
 * five screens Accueil promotes: the feature that offers a result never learns that the Plus tab is
 * what owns the screen behind it, and `SearchNavigatorImpl` in `app/` is the only thing allowed to
 * know both. The topic enum crosses because it is this feature's own, and the impl is where it turns
 * into a destination — exhaustively, so a topic added without somewhere to send it is a compile
 * error rather than a row that does nothing.
 */
interface SearchNavigator {
    fun navigateToHappening(happeningId: String)

    fun navigateToTopic(topic: SearchTopicUiModel)

    fun navigateBack()
}
