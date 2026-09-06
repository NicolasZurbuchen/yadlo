package io.nicolaszurbuchen.yadlo.design.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A rectangle whose bottom edge is a wave — DECISIONS.md § The chrome ends in a wave.
 *
 * [depth] is taken *out of* the height rather than added to it, so a caller pads by the same amount
 * to keep the flat part the size it was.
 *
 * [wavelength] is a target rather than a measurement: the shape divides the width by it, rounds to
 * a whole number of crests and stretches them to fit, because a wave cut off mid-period leaves one
 * edge on a crest and the other in a trough.
 */
data class WaveEdge(
    val depth: Dp = WAVE_DEPTH,
    val wavelength: Dp = WAVE_LENGTH,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val depthPx = with(density) { depth.toPx() }
        val flatBottom = size.height - depthPx

        // Nothing to draw a wave into. Guarded here rather than at the call site because a shape is
        // asked for its outline before its content is laid out, and a bar's first frame is empty.
        if (flatBottom <= 0f || size.width <= 0f) return Outline.Rectangle(size.toRect())

        val target = with(density) { wavelength.toPx() }
        val crests = (size.width / target).roundToInt().coerceAtLeast(1)
        val period = size.width / crests

        val path =
            Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, flatBottom)

                // Right to left, one symmetrical S per period: down to the trough halfway along,
                // back up to the baseline at the end of it. Quadratics rather than a sine sampled
                // into segments — two control points describe the curve exactly and cost nothing
                // when the bar is re-measured at a new width.
                repeat(crests) { index ->
                    val end = size.width - period * (index + 1)
                    val middle = end + period / 2f

                    quadraticTo(middle + period / 4f, flatBottom + depthPx * 2f, middle, flatBottom)
                    quadraticTo(middle - period / 4f, flatBottom - depthPx * 2f, end, flatBottom)
                }

                close()
            }

        return Outline.Generic(path)
    }
}

/** Paints [color] behind the caller and ends it in a wave. Padding for [depth] is the caller's job. */
fun Modifier.waveEdgeBackground(
    color: Color,
    depth: Dp = WAVE_DEPTH,
): Modifier = background(color = color, shape = WaveEdge(depth = depth))

// Deep enough to read as a shape rather than as a printing fault at arm's length in sunlight, and
// what every caller pads by — so it is what the decoration costs on every screen.
val WAVE_DEPTH = 12.dp

// About four crests on a phone. Fewer reads as a scallop and competes with the content; many more
// turn the edge into a texture that shimmers at small sizes.
val WAVE_LENGTH = 96.dp
