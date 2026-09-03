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
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.scene.rememberSceneState
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.rememberNavigationEventState
import io.nicolaszurbuchen.yadlo.infra.platform.BackHandler

/**
 * How long a screen takes to cross the window, and the one number the whole app's navigation moves
 * on. The chrome in `MainScaffold` leaves and returns on it too, so that a tab root and the bars
 * belonging to it travel as one thing rather than two.
 *
 * Material's own duration for a full-screen enter. Long enough to be followed across the width of a
 * phone, short enough that reaching a fiche four times in a row does not feel like waiting.
 */
const val NAV_SLIDE_MILLIS = 300

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
    val sceneState =
        rememberSceneState(
            entries = entries,
            sceneStrategies = listOf(SinglePaneSceneStrategy()),
            onBack = onBack,
        )
    val scene = sceneState.currentScene

    // **The back gesture is a button press, and the screen does not move under the finger.** The
    // display's own handler reports the gesture's progress and seeks the pop animation to it, so a
    // half-finished swipe leaves the two screens parked half way across the window. Wiring a plain
    // BackHandler instead — and passing a gesture state nothing ever drives — leaves that state
    // idle, which is what makes a release run the pop below from its start rather than from
    // wherever the thumb stopped.
    //
    // This is the shape of `NavDisplay(entries, onBack)`'s own body, minus its `NavigationBackHandler`.
    // Taking the overload below is the only way in: the wiring is not a parameter of the short one.
    val gestureState =
        rememberNavigationEventState(
            currentInfo = SceneInfo(scene),
            backInfo = sceneState.previousScenes.map { SceneInfo(it) },
        )

    // One press pops one screen, however many entries the scene turns out to be holding. Guarded on
    // the scene rather than the list, so the tab roots stay unpoppable and back there falls through
    // to the shell.
    BackHandler(enabled = scene.previousEntries.isNotEmpty()) {
        repeat(entries.size - scene.previousEntries.size) { onBack() }
    }

    // **One transition for the whole app, written here rather than per entry.** Navigation 3 ships
    // a different default on each platform — Android fades and shrinks the outgoing screen towards
    // the middle, iOS slides — so leaving them alone means the same push looks like two different
    // apps. Spelling it out once is also what keeps a screen from acquiring an animation of its own
    // as a side effect of where it happens to be declared.
    NavDisplay(
        sceneState = sceneState,
        navigationEventState = gestureState,
        transitionSpec = { slide(SlideDirection.Left) },
        popTransitionSpec = { slide(SlideDirection.Right) },
        // Unreachable while the gesture state stays idle, and set anyway: a Scene that asks for a
        // predictive pop should get the pop this app has, not the platform's own.
        predictivePopTransitionSpec = { slide(SlideDirection.Right) },
        modifier = modifier,
    )
}

/**
 * The screen being opened slides in from the edge the screen being left slides out towards, both
 * covering the full width, so the two read as one sheet moving rather than two things happening.
 */
private fun AnimatedContentTransitionScope<Scene<NavKey>>.slide(towards: SlideDirection): ContentTransform =
    slideIntoContainer(towards, tween(NAV_SLIDE_MILLIS)) togetherWith
        slideOutOfContainer(towards, tween(NAV_SLIDE_MILLIS))
