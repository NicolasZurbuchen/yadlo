package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloRowUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One saved Slot, in the Programme's row vocabulary — DECISIONS.md § Mon Yadlo layout.
 *
 * One thing the Programme's row has is gone: the **span bar**, which placed a Slot against the
 * whole day's axis and answers "what else is on at four" — a question about a day you are choosing
 * from, not one you have already chosen. The price stayed out for a while on the same reasoning and
 * came back, because that reasoning was wrong: the decision was made when the heart was tapped, and
 * the coins in your pocket were not.
 *
 * The chevron is centred over the row's whole height rather than pinned to its first line, as on the
 * Programme, which is what makes it read as belonging to the row.
 *
 * Past rows dim and stay here too, and on this screen that is the point: by Sunday the Plan is
 * mostly what you went to.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlannedSlotRow(
    row: MonYadloRowUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = MaterialTheme.categoryColors.forId(row.categoryId)
    val isOver = row.state is SlotLiveStateUiModel.Over

    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(row.happeningId) }
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(vertical = MaterialTheme.spacing.sm),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(top = MARK_TOP_OFFSET)
                    .size(CATEGORY_MARK_SIZE)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(category.fill),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = row.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.appColors.textPrimary,
                )

                row.stateLabel?.let { label ->
                    SlotStatePill(label = label, state = row.state)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                Text(
                    text = row.timeText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.appColors.textSecondary,
                )

                // The Category written out beside the mark that colours it, as on the Programme.
                // In July sun, on a phone, it is the word that survives.
                Text(
                    text = "· ${row.categoryName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textTertiary,
                )
            }
        }

        row.priceText?.let { price ->
            Text(
                text = price.asString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.align(Alignment.CenterVertically).size(CHEVRON_SIZE),
        )
    }
}

private val CATEGORY_MARK_SIZE = 10.dp

/** The Programme's chevron, at the Programme's size, for the row it is a copy of. */
private val CHEVRON_SIZE = 24.dp

/**
 * Half of titleMedium's 24sp line box less half the mark, so the square centres on the *first* line
 * of the name rather than floating at the top edge of one that wraps to two.
 */
private val MARK_TOP_OFFSET = 7.dp

/** The Programme's value, so a row that has dimmed on the list is as dim here. */
private const val PAST_ALPHA = 0.45f
