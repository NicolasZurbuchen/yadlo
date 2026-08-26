package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import io.nicolaszurbuchen.yadlo.app.design.theme.sizing
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotTimeBar
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloRowUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One saved Slot, in the Programme's row vocabulary — DECISIONS.md § Mon Yadlo layout.
 *
 * **The span bar is back**, having been left out on the reasoning that it answers "what else is on
 * at four" — a question about a day you are choosing from rather than one you have chosen. That was
 * half right. You are not choosing any more, but on the Sunday afternoon "how much of this is left,
 * and what have I got after it" is exactly what a Plan is open for, and it is the same question the
 * price was left out over and came back on: the decision was made when the heart was tapped, and
 * the coins in your pocket were not.
 *
 * Its axis is not the Programme's. Three days are on screen at once here, so every bar is measured
 * against one span covering all of them — DECISIONS.md § Mon Yadlo's bars share one axis.
 *
 * The chevron is centred over the row's whole height rather than pinned to its first line, as on the
 * Programme, which is what makes it read as belonging to the row.
 *
 * **Three lines with a right-hand column down two of them**, the Programme's arrangement exactly:
 * the Category with the live state answering it, the name with what it costs, and the time alone.
 * Left is what the thing is, right is what to do about it. The name and the pill and the price were
 * competing for one line, which holds for `AMC` and breaks for a five-word artist name.
 *
 * Past rows dim and stay here too, and on this screen that is the point: by Sunday the Plan is
 * mostly what you went to.
 */
@Composable
fun PlannedSlotRow(
    row: MonYadloRowUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = MaterialTheme.categoryColors.forId(row.categoryId)
    val isOver = row.slot.state is SlotLiveStateUiModel.Over

    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(row.happeningId) }
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(vertical = ROW_VERTICAL_PADDING),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(MaterialTheme.sizing.categoryMark)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(category.fill),
                )

                // The Category written out beside the mark that colours it, as on the Programme.
                // In July sun, on a phone, it is the word that survives.
                Text(
                    text = row.categoryName.uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.appColors.textTertiary,
                    modifier = Modifier.weight(1f),
                )

                row.stateLabel?.let { label ->
                    SlotStatePill(label = label, state = row.slot.state)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Baselines rather than tops or centres: a name long enough to wrap should still
                // have its price beside its first line, not floating halfway down two.
                Text(
                    text = row.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f).alignByBaseline(),
                )

                row.priceText?.let { price ->
                    Text(
                        text = price.asString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }

            Text(
                text = row.slot.timeText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.appColors.textSecondary,
            )

            SlotTimeBar(segments = listOf(row.slot), categoryFill = category.fill)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.align(Alignment.CenterVertically).size(CHEVRON_SIZE),
        )
    }
}

/**
 * The Programme's row padding, which this was eight against. Two rows of the same vocabulary should
 * breathe the same, and at eight the rule between two entries sat closer to the text above it than
 * to the gap it was meant to be in the middle of.
 *
 * Internal because the scale in the chrome is inset past this row's chevron column, and a scale
 * offset from the axis it labels is worse than no scale.
 */
private val ROW_VERTICAL_PADDING = 12.dp

/** The Programme's chevron, at the Programme's size, for the row it is a copy of. */
internal val CHEVRON_SIZE = 24.dp

/** The Programme's value, so a row that has dimmed on the list is as dim here. */
private const val PAST_ALPHA = 0.45f
