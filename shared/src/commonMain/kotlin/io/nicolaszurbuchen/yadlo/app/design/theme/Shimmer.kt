package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The loading placeholder, as a token rather than a component.
 *
 * Every screen behind a Plus row waits for the same bundle, so every one of them wants the same
 * thing while it waits: **the shape of the answer, not a spinner in the middle of it.** A centred
 * `CircularProgressIndicator` throws away everything already known about a screen — that it is a
 * paragraph over three cards, that a heading sits above each — and replaces it with a symbol that
 * could be any screen in any app. Drawing the frame instead means the real content arrives *into* a
 * layout the eye has already settled on rather than replacing one.
 *
 * What the design system owns is the pulse and the block; what each screen owns is its own geometry,
 * because that geometry is the entire point.
 */
@Composable
fun ShimmerPulse(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = MIN_ALPHA,
        targetValue = MAX_ALPHA,
        animationSpec = infiniteRepeatable(animation = tween(PULSE_MILLIS), repeatMode = RepeatMode.Reverse),
        label = "shimmerAlpha",
    )

    CompositionLocalProvider(LocalShimmerAlpha provides alpha, content = content)
}

/**
 * Paints one placeholder, breathing in time with every other placeholder under the same
 * [ShimmerPulse].
 *
 * It fades rather than sweeping a gradient across the row. A travelling highlight needs a brush
 * animated per-frame across every placeholder, which on the slowest device this app supports is work
 * spent on the one screen that is by definition waiting for something else. One alpha, provided
 * once, animates a single value however many blocks read it.
 *
 * Size it at the call site — a placeholder that does not match the thing it stands in for is what
 * makes the content visibly jump when it lands.
 */
@Composable
fun Modifier.shimmerBlock(shape: Shape = RoundedCornerShape(BLOCK_CORNER)): Modifier =
    clip(shape).background(MaterialTheme.appColors.textTertiary.copy(alpha = LocalShimmerAlpha.current))

/**
 * Static rather than animated when no [ShimmerPulse] is above: a preview or a screenshot test still
 * draws the frame, it simply does not breathe.
 */
private val LocalShimmerAlpha = compositionLocalOf { MIN_ALPHA }

// Never fully transparent and never fully opaque: at 0 the blocks disappear and the layout reads as
// empty rather than as loading, and at 1 a tertiary-coloured bar is mistakable for real text.
private const val MIN_ALPHA = 0.10f
private const val MAX_ALPHA = 0.28f

// Slower than the 700ms a list of network results would use. This covers a cache read that is
// usually over in a frame or two, and a fast pulse on a placeholder nobody sees for long only reads
// as flicker.
private const val PULSE_MILLIS = 1000

// Softer than the app's smallest shape token, which is sized for cards. A placeholder standing in
// for a line of text wants the corner of a text run, not of a container.
private val BLOCK_CORNER = 4.dp
