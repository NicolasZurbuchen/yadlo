package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation

/**
 * Both screens open a fiche, and it is the same fiche the Programme opens — which is also the only
 * place either of them can be unsaved again. The feature never learns that `HappeningDestination`
 * belongs to another one.
 */
interface MonYadloNavigator {
    fun navigateToHappening(happeningId: String)

    fun navigateToWishlist()

    fun navigateBack()
}
