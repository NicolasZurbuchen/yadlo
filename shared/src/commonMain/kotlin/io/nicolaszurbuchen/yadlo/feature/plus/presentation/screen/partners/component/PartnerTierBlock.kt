package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnerTierUiModel

/**
 * One tier, as a grid of tappable cards — [columns] wide, each card a logo on white.
 *
 * **[columns] is the tier's rank, expressed as room.** The tiers are the hierarchy the sponsors paid
 * into, and the top of it is drawn two across while the rest are three: a *Sponsor général* gets a
 * card half again as wide and a third taller than a *Partenaire*, which is the same statement the
 * order of the sections already makes, said in the one other language a layout has. The screen
 * decides which tiers those are — see PartnersScreen.
 *
 * The cell's height follows the count rather than being passed alongside it, because the two only
 * ever move together: a wider card in the same 72dp band would be a letterbox, and every logo in the
 * bank would end up width-bound inside it.
 *
 * Chunked rows rather than a lazy grid, because this sits inside the page's own scroll: a lazy grid
 * given an unbounded height either crashes or becomes a window into itself, and there are never
 * more than twenty cards to lay out at once.
 */
@Composable
fun PartnerTierBlock(
    tier: PartnerTierUiModel,
    columns: Int,
    onPartnerClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        YadloSectionHeader(title = tier.name)

        tier.members.chunked(columns).forEach { rowMembers ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowMembers.forEach { partner ->
                    PartnerCard(
                        partner = partner,
                        onClick = onPartnerClick,
                        modifier = Modifier.weight(1f).height(cellHeightFor(columns)),
                    )
                }

                // A short last row keeps its cells the width of the ones above rather than
                // stretching them, so a tier of four does not read as two different card sizes.
                repeat(columns - rowMembers.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Two across is the prominent shape and three the ordinary one, and each has the height that keeps
 * its card near enough to square for a logo to have somewhere to be.
 *
 * The three-column figure is unchanged from when these cards held names: it is two lines of a
 * company name at the largest accessibility text size that still fits three across, which is also
 * what the name fallback still needs.
 */
private fun cellHeightFor(columns: Int) = if (columns <= PROMINENT_COLUMNS) PROMINENT_CELL_HEIGHT else CELL_HEIGHT

private const val PROMINENT_COLUMNS = 2

private val PROMINENT_CELL_HEIGHT = 96.dp

private val CELL_HEIGHT = 72.dp
