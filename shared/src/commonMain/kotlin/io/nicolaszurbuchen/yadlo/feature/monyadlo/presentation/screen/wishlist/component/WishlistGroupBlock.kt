package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.StandCard
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.WishlistGroupUiModel

/**
 * One Category of the Wishlist, with its saved Stands under it.
 *
 * Category is the app's only grouping axis, so *restauration* and *créateurs* here are the same
 * groups the Programme filters by rather than a second taxonomy grown for one screen.
 *
 * **The same cards Plus draws, not rows of its own.** *Reversed: these were text rows separated by
 * hairlines.* The argument for rows was that a Wishlist is a list you compare across, and it was
 * answering the wrong question — you compare across a Programme to choose what to do at four
 * o'clock, and you open *À essayer* on the site to find the stall you kept among forty you did not.
 * That is matching a picture to a thing in front of you, which no amount of text does. It also means
 * a Stand looks the same on the screen it was saved from and the screen it was saved to.
 *
 * The hairlines went with the rows. Cards are already separate objects, and a rule between two of
 * them is a second separator doing the first one's job.
 */
@Composable
fun WishlistGroupBlock(
    group: WishlistGroupUiModel,
    onStandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier = modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.md),
    ) {
        YadloSectionHeader(title = group.name)

        group.stands.forEach { stand ->
            StandCard(stand = stand, onClick = onStandClick)
        }
    }
}
