package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

/**
 * Accueil navigates two different ways: the hero switches *tab*, which only the shell knows how to
 * do, and the annonces action pushes onto Accueil's own stack. Both live behind this one interface
 * so the feature never learns which is which.
 *
 * **The five promoted destinations are screens the Plus tab owns, and they are pushed rather than
 * switched to.** A tab switch would land the reader on Plus's root with the screen stacked over it,
 * so backing out of *Paiement* would leave them somewhere they never chose to be. Pushed onto
 * Accueil's own stack, back returns to Accueil — the same rule the fiche already follows when it is
 * opened from two different tabs.
 *
 * The keys stay behind this interface for the usual reason: `HomeNavigatorImpl` lives in `app/` and
 * is the only thing that may know both features exist.
 */
interface HomeNavigator {
    fun navigateToSearch()

    fun navigateToProgramme()

    fun navigateToAnnouncements()

    fun navigateToPayment()

    fun navigateToAccess()

    fun navigateToVolunteering()

    fun navigateToContact()

    fun navigateToStory()

    fun navigateBack()
}
