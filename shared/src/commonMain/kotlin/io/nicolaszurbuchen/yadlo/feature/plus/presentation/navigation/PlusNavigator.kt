package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel

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
    fun navigateToStands(kind: StandsKindUiModel)

    fun navigateToPayment()

    fun navigateToAccess()

    fun navigateToAccessibility()

    fun navigateToHours()

    fun navigateToAssistance()

    fun navigateToFaq()

    fun navigateToStory()

    fun navigateToPartners()

    fun navigateToContact()

    fun navigateToVolunteering()

    fun navigateToResponsible()

    fun navigateToAbout()

    fun navigateToPrivacy()

    fun navigateToHappening(happeningId: String)

    fun navigateBack()
}
