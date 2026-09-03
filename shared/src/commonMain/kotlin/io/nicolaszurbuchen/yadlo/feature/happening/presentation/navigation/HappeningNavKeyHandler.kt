package io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningRoute
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningViewModel
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class HappeningNavKeyHandler(
    private val navigator: HappeningNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        // The id reaches the Store through the ViewModel rather than through the Route, because a
        // Route may only take lambdas, a Modifier or a ViewModel. The destination survives process
        // death, so a restored fiche rebuilds against the same Happening.
        // **A fade rather than the platform push, and only on this destination.**
        //
        // The photograph travels between the list and this screen, and a container that slides or
        // scales at the same time is a second motion the eye has to reconcile with the first — the
        // list visibly shrinking away while the picture flies over it is three things happening
        // rather than one. Fading both screens leaves the picture as the only thing that moves,
        // which is the whole point of a shared element.
        //
        // Every other push in the app keeps the platform default. This is the only screen with
        // something travelling into it.
        entry<HappeningDestination>(
            metadata =
                NavDisplay.transitionSpec { CONTAINER_FADE } +
                    NavDisplay.popTransitionSpec { CONTAINER_FADE } +
                    NavDisplay.predictivePopTransitionSpec { CONTAINER_FADE },
        ) { destination ->
            HappeningRoute(
                onNavigateBack = { navigator.navigateBack() },
                viewModel = koinViewModel<HappeningViewModel>(parameters = { parametersOf(destination.happeningId) }),
            )
        }
    }
}

/**
 * Both halves fade, so the only thing that moves between the list and the fiche is the photograph.
 *
 * The duration is the shared element’s own default, so the fade and the travel finish together — a
 * container still fading under a picture that has already landed reads as a stutter at the end.
 */
private val CONTAINER_FADE = fadeIn(tween(TRANSITION_MILLIS)) togetherWith fadeOut(tween(TRANSITION_MILLIS))

private const val TRANSITION_MILLIS = 300
