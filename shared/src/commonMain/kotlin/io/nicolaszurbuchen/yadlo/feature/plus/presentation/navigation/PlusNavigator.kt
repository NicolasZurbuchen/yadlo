package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

/**
 * Plus is the one tab that is mostly a table of contents, so it is also the one with a navigator
 * this wide. One method per entry rather than a single `navigateTo(entry)`: the feature would then
 * have to hand its own destination type across the boundary, and the point of this interface is
 * that a feature never learns which key another one owns — which is what lets
 * [navigateToHappening] reach the fiche the Programme also opens.
 *
 * [navigateToResponsible] is the exception and earns it: several entries are the same screen with
 * different words, and giving each its own method would be inventing a distinction the app does
 * not have.
 */
interface PlusNavigator {
    /**
     * Two methods for one screen, matching the two keys behind them: which half is being asked
     * for is the whole difference, and a parameter here would be a value this interface then has
     * to name a type for — the type that used to end up serialized into the back stack.
     */
    fun navigateToFoodStands()

    fun navigateToMakerStands()

    fun navigateToPayment()

    fun navigateToAccess()

    fun navigateToHours()

    fun navigateToAssistance()

    fun navigateToFaq()

    fun navigateToStory()

    fun navigateToPartners()

    fun navigateToContact()

    fun navigateToVolunteering()

    fun navigateToResponsible()

    fun navigateToNotifications()

    fun navigateToAbout()

    fun navigateToPrivacy()

    fun navigateToClearData()

    fun navigateToHappening(happeningId: String)

    fun navigateBack()
}
