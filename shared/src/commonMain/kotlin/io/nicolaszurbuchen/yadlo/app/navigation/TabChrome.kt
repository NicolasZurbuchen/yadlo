package io.nicolaszurbuchen.yadlo.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How much of the window the tab shell's own bars cover, top and bottom.
 *
 * **Published rather than subtracted, because the shell no longer resizes anything.** [MainScaffold]
 * used to be a `Scaffold`, so the top and bottom bars took their height out of the window and the
 * four tab roots were handed what was left. That made the viewport a function of how deep the
 * current tab was: pushing a fiche removed both bars in the same frame the push started, so the
 * screen being animated away was re-measured taller mid-transition. It jumped up under the status
 * bar on the way out, and on the way back a list that had been scrolled near its end came back at a
 * different offset, because a shorter viewport had clamped the scroll and clamping is not
 * reversible.
 *
 * The bars are drawn over the graph now, and this is what a tab root adds to its own content padding
 * so its first and last rows still clear them. The value is measured from the bars themselves and
 * does not change when they are hidden — which is the whole point, since it is the screen underneath
 * a push that must not move.
 *
 * Zero everywhere but under [MainScaffold]: a detail screen is full-bleed and applies its own
 * window insets, and a preview has no shell at all.
 */
data class TabChromeInsets(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
)

val LocalTabChromeInsets = compositionLocalOf { TabChromeInsets() }

/**
 * A tab root's own content padding, with the shell's bars added to it.
 *
 * Every tab root scrolls under the bars rather than beside them, so the padding is what keeps the
 * first row out from behind the title and the last row off the tab bar.
 */
@Composable
@ReadOnlyComposable
fun tabContentPadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues {
    val chrome = LocalTabChromeInsets.current

    return PaddingValues(start = start, top = top + chrome.top, end = end, bottom = bottom + chrome.bottom)
}
