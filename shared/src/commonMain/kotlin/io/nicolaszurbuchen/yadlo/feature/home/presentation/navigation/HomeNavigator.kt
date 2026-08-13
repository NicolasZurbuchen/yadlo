package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

/**
 * Accueil's hero sends the visitor to another *tab*, not to a destination on its own stack, which
 * is why this is a navigator rather than a destination of its own: only the shell knows the tabs
 * exist.
 */
interface HomeNavigator {
    fun navigateToProgramme()

    fun navigateToMonYadlo()
}
