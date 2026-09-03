package io.nicolaszurbuchen.yadlo.infra.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent.Companion.EDGE_LEFT

/**
 * Renders already-decorated entries. It owns no stack and knows nothing about tabs — the caller
 * decides which set of entries is visible, which is what lets four tabs each keep their own
 * history while sharing one display.
 *
 * Entries come in rather than a back stack because a display that is handed a different stack has
 * no way to swap the decorator state that belongs to it. See [rememberNavEntries].
 */
@Composable
fun NavGraph(
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // **One transition for the whole app, written here rather than per entry.** Navigation 3 ships
    // a different default on each platform — Android fades and shrinks the outgoing screen towards
    // the middle, iOS slides — so leaving them alone means the same push looks like two different
    // apps. Spelling it out once is also what keeps a screen from acquiring an animation of its own
    // as a side effect of where it happens to be declared.
    NavDisplay(
        entries = entries,
        onBack = { onBack() },
        transitionSpec = { slide(SlideDirection.Left) },
        popTransitionSpec = { slide(SlideDirection.Right) },
        // A back swipe from the right edge is the mirror of one from the left, and the screen has
        // to follow the finger rather than run away from it.
        predictivePopTransitionSpec = { edge ->
            slide(if (edge == EDGE_LEFT) SlideDirection.Right else SlideDirection.Left)
        },
        modifier = modifier,
    )
}

/**
 * The screen being opened slides in from the edge the screen being left slides out towards, both
 * covering the full width, so the two read as one sheet moving rather than two things happening.
 */
private fun AnimatedContentTransitionScope<Scene<NavKey>>.slide(towards: SlideDirection): ContentTransform =
    slideIntoContainer(towards, tween(SLIDE_MILLIS)) togetherWith
        slideOutOfContainer(towards, tween(SLIDE_MILLIS))

// Material's own duration for a full-screen enter. Long enough to be followed across the width of a
// phone, short enough that reaching a fiche four times in a row does not feel like waiting.
private const val SLIDE_MILLIS = 300
