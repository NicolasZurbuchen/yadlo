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

// Long enough to be followed across the width of a phone, short enough that reaching a fiche four
// times in a row does not feel like waiting. The chrome in MainScaffold moves on it too.
const val NAV_SLIDE_MILLIS = 300

/**
 * Renders one back stack's already-decorated entries. Call it with the entries of a single stack —
 * see [rememberNavEntries] for why that matters.
 *
 * [slideTowards] is the side the next forward move travels towards, which is the caller's to decide:
 * a push and a sibling tab mean different things and arrive from different edges. Going back is
 * always its mirror.
 */
@Composable
fun NavGraph(
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit,
    slideTowards: SlideDirection,
    modifier: Modifier = Modifier,
) {
    val sceneState =
        rememberSceneState(
            entries = entries,
            sceneStrategies = listOf(SinglePaneSceneStrategy()),
            onBack = onBack,
        )
    val scene = sceneState.currentScene

    // NavDisplay(entries, onBack) would register NavigationBackHandler here and feed this state the
    // gesture's progress. Left undriven on purpose — DECISIONS.md § One transition, spelled out once
    // — and this longer overload is the only one that lets a caller do that.
    val gestureState =
        rememberNavigationEventState(
            currentInfo = SceneInfo(scene),
            backInfo = sceneState.previousScenes.map { SceneInfo(it) },
        )

    // A scene can hold more than one entry, and one press pops one scene.
    BackHandler(enabled = scene.previousEntries.isNotEmpty()) {
        repeat(entries.size - scene.previousEntries.size) { onBack() }
    }

    NavDisplay(
        sceneState = sceneState,
        navigationEventState = gestureState,
        transitionSpec = { slide(slideTowards) },
        popTransitionSpec = { slide(SlideDirection.Right) },
        // Unreachable while the gesture state stays idle, and set so a Scene that asks for one is
        // not handed the platform default instead.
        predictivePopTransitionSpec = { slide(SlideDirection.Right) },
        modifier = modifier,
    )
}

private fun AnimatedContentTransitionScope<Scene<NavKey>>.slide(towards: SlideDirection): ContentTransform =
    slideIntoContainer(towards, tween(NAV_SLIDE_MILLIS)) togetherWith
        slideOutOfContainer(towards, tween(NAV_SLIDE_MILLIS))
