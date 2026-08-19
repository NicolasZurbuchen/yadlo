package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnerTierUiModel

/**
 * One tier, as a grid of tappable cards.
 *
 * **Names, not logos.** Every partner in the 2026 content has a null logo — the files exist on the
 * association's site as untitled images and none has been supplied — so each card draws the name
 * that was transcribed by hand. That is the right fallback rather than an empty frame: the name is
 * what the logo was standing for, and a grid of grey rectangles would say the app is broken.
 *
 * Chunked rows rather than a lazy grid, because this sits inside the page's own scroll: a lazy grid
 * given an unbounded height either crashes or becomes a window into itself, and there are never
 * more than twenty cards to lay out at once.
 */
@Composable
fun PartnerTierBlock(
    tier: PartnerTierUiModel,
    onPartnerClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        YadloSectionHeader(title = tier.name)

        tier.members.chunked(COLUMNS).forEach { rowMembers ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowMembers.forEach { partner ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(CELL_HEIGHT)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.appColors.surface)
                                .clickable { onPartnerClick(partner.url) }
                                .padding(MaterialTheme.spacing.sm),
                    ) {
                        Text(
                            text = partner.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.appColors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // A short last row keeps its cells the width of the ones above rather than
                // stretching them, so a tier of four does not read as two different card sizes.
                repeat(COLUMNS - rowMembers.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private const val COLUMNS = 3

// Tall enough for two lines of a company name at the largest accessibility text size that still
// fits three across; below this "Commune de Préverenges" clips rather than wraps.
private val CELL_HEIGHT = 72.dp
