package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * The one control that saves anything — DECISIONS.md § Two verbs: Plan and Wishlist. Filled means
 * kept, outlined means not, and tapping it again is how something is removed.
 *
 * **Filled versus outlined carries the state on its own**, which is why [tint] belongs to the call
 * site. On a date row the heart sits on the page ground and can take the accent; in the toolbar it
 * sits on a surface that has taken the Category's own fill once collapsed, where an accent rose
 * would land on the `musique` magenta — the collision DECISIONS.md § Open already flags. The two
 * never appear together: a Stand is kept whole and has no date rows, everything else keeps its Slots
 * one at a time and has no heart in the bar.
 *
 * [contentDescription] is null where the tap target is the whole row rather than this icon: the row
 * names its own action, and a second announcement for the mark inside it says the same thing twice.
 */
@Composable
fun SavedHeart(
    isSaved: Boolean,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}
