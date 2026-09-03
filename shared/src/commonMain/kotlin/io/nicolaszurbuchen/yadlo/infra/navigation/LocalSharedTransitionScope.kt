package io.nicolaszurbuchen.yadlo.infra.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

/**
 * The scope a shared element needs, handed down from [NavGraph] so a picture deep inside a list
 * can join a transition without every layer between them taking a parameter for it.
 *
 * **Null outside the display, which is the point.** A card is drawn in previews and screenshot
 * tests with no navigation above it, and `LocalNavAnimatedContentScope` throws rather than
 * returning null when it is read there. This one going null first is what lets a caller skip the
 * whole business — see `ContentImage`.
 */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
