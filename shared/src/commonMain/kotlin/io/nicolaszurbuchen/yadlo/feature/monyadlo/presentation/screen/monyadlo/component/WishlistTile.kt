package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.component.YadloHero
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_wishlist_empty
import yadlo.shared.generated.resources.mon_yadlo_wishlist_subtitle_one
import yadlo.shared.generated.resources.mon_yadlo_wishlist_subtitle_other
import yadlo.shared.generated.resources.mon_yadlo_wishlist_title

/**
 * The way to the other half of Mon Yadlo — one full-width block, never a second tab or a segmented
 * control (DECISIONS.md § Two verbs: Plan and Wishlist).
 *
 * It sits **above** the timeline. *À essayer* is a checklist consulted while standing on the site,
 * and by the Sunday the timeline above it would be three days of finished rows to scroll past.
 *
 * **A hero, not a Plus row.** *Reversed: it was a surface-coloured row with a leading icon.* On Plus
 * that shape is right, because a Plus row is one of eight and the icon is what tells them apart in a
 * column. Here it is the only thing above the timeline and it is the entire other half of the tab,
 * so a row-shaped block made the screen look like a list that happened to start with a link. The
 * hero is what the app already uses for "the answer, before the page that supports it", and *À
 * essayer · trois stands gardés* is exactly that shape — it is also the treatment Accueil gives the
 * one block it wants you to tap, which is what this is.
 *
 * The leading fork-and-knife goes with the change, and that is the cost. It tied this to
 * *Nourriture & boissons* in Plus, which the words still do; a hero has no icon slot and inventing
 * one for a single caller would make every other hero in the app answer a question it does not have.
 *
 * The plural is two strings rather than one with a `(s)`: French puts 1 in the singular, and the
 * zero case never reaches them — it says so in words instead, because a lone `0` is the one number
 * that reads as a fault.
 */
@Composable
fun WishlistTile(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    YadloHero(
        title = stringResource(Res.string.mon_yadlo_wishlist_title),
        body =
            when (count) {
                0 -> stringResource(Res.string.mon_yadlo_wishlist_empty)
                1 -> stringResource(Res.string.mon_yadlo_wishlist_subtitle_one, count)
                else -> stringResource(Res.string.mon_yadlo_wishlist_subtitle_other, count)
            },
        onClick = onClick,
        modifier = modifier,
    )
}
