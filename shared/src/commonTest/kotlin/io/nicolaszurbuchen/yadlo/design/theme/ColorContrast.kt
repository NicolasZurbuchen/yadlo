package io.nicolaszurbuchen.yadlo.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.cbrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

// Colour measurement shared by AppColorTest and CategoryColorTest. Two different questions are
// being asked and they need two different measures: contrast answers "can this be read", and
// perceptual distance answers "can these two be told apart", which is not the same thing — two
// colours can differ wildly in hue and still have near-identical contrast against a background.

/** WCAG 2.1 contrast ratio, 1:1 to 21:1. */
internal fun contrastRatio(
    a: Color,
    b: Color,
): Double {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)

    return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
}

/**
 * Perceptual distance in OKLab, scaled to roughly the familiar 0–100 ΔE range.
 *
 * OKLab rather than raw RGB distance because RGB says orange and brown are far apart and says two
 * blues are close, which is the opposite of what an eye reports — and telling category dots apart
 * at arm's length is the entire reason the palette was chosen the way it was.
 */
internal fun perceptualDistance(
    a: Color,
    b: Color,
): Double {
    val (l1, a1, b1) = oklab(a)
    val (l2, a2, b2) = oklab(b)

    return sqrt((l1 - l2).pow(2) + (a1 - a2).pow(2) + (b1 - b2).pow(2)) * 100
}

/**
 * Flattens a translucent colour onto an opaque one, the way the compositor will.
 *
 * A scrim's contrast is meaningless until it is composited: the value itself is 60% of a near-black,
 * which measures as near-black and tells us nothing about what happens over a photograph.
 */
internal fun Color.over(background: Color): Color =
    Color(
        red = red * alpha + background.red * (1 - alpha),
        green = green * alpha + background.green * (1 - alpha),
        blue = blue * alpha + background.blue * (1 - alpha),
    )

internal fun Double.format(): String = ((this * 100).toInt() / 100.0).toString()

/** WCAG 2.1 relative luminance, sRGB. */
private fun relativeLuminance(color: Color): Double =
    0.2126 * toLinear(color.red) + 0.7152 * toLinear(color.green) + 0.0722 * toLinear(color.blue)

private fun toLinear(component: Float): Double {
    val c = component.toDouble()

    return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
}

private fun oklab(color: Color): Triple<Double, Double, Double> {
    val r = toLinear(color.red)
    val g = toLinear(color.green)
    val b = toLinear(color.blue)

    val l = cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b)
    val m = cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b)
    val s = cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b)

    return Triple(
        0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s,
        1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s,
        0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s,
    )
}
