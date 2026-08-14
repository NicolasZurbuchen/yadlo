package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

/**
 * Accueil navigates two different ways: the hero switches *tab*, which only the shell knows how to
 * do, and the annonces action pushes onto Accueil's own stack. Both live behind this one interface
 * so the feature never learns which is which.
 */
interface HomeNavigator {
    fun navigateToProgramme()

    fun navigateToAnnouncements()

    fun navigateBack()
}
