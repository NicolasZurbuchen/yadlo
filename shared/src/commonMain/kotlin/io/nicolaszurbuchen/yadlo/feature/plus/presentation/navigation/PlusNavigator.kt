package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

/**
 * Plus is the one tab that is mostly a table of contents, so it is also the one with a navigator
 * this wide. One method per entry rather than a single `navigateTo(entry)`: the feature would then
 * have to hand its own destination type across the boundary, and the point of this interface is
 * that a feature never learns which key another one owns — which is what lets
 * [navigateToHappening] reach the fiche the Programme also opens.
 */
interface PlusNavigator {
    fun navigateToStands()

    fun navigateToPayment()

    fun navigateToAccess()

    fun navigateToAccessibility()

    fun navigateToHours()

    fun navigateToAssistance()

    fun navigateToFaq()

    fun navigateToHappening(happeningId: String)

    fun navigateBack()
}
