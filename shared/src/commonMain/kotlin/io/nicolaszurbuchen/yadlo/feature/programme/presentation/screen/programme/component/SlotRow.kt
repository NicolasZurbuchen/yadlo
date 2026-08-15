package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.SlotRowUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One Slot on the day's list — layout B2.
 *
 * A row rather than a card: the Programme exists to weigh Slots against each other, which wants a
 * shared left edge and as little furniture between neighbours as possible. Cards were measured at
 * +32% vertical space on the Saturday and separate exactly what this screen is for comparing.
 *
 * Past rows dim and stay. By 21:00 on the Saturday that is most of the list, and that is accepted:
 * reading what has already happened is part of reading the day you are standing in.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SlotRow(
    row: SlotRowUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = MaterialTheme.categoryColors.forId(row.categoryId)
    val isOver = row.state is SlotLiveStateUiModel.Over

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(row.happeningId) }
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
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

                    // The Category written out, beside the mark that colours it. Colour is never
                    // the only carrier: in July sun, on a phone, it is the word that survives.
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
        }

        // Only while it is on. The bar spans the Slot itself rather than the day, which is the
        // difference between B2 and the option it replaced: it answers "how much of this is left",
        // not "where in the afternoon does this sit" — the second is a time axis, and a time axis
        // is the horizontal width B2 exists to give back to the text.
        val progress =
            when (val state = row.state) {
                is SlotLiveStateUiModel.Running -> state.progress
                is SlotLiveStateUiModel.Ending -> state.progress
                else -> null
            }

        progress?.let {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(PROGRESS_HEIGHT)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.appColors.surfaceRaised),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(it)
                            .height(PROGRESS_HEIGHT)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(category.fill),
                )
            }
        }
    }
}

private val CATEGORY_MARK_SIZE = 10.dp

/**
 * Half of titleMedium's 24sp line box less half the mark, so the square centres on the *first* line
 * of the name rather than floating at the top edge of one that wraps to two.
 */
private val MARK_TOP_OFFSET = 7.dp

private val PROGRESS_HEIGHT = 6.dp

/** The prototype's 0.42, rounded. Low enough to recede, high enough to still be read. */
private const val PAST_ALPHA = 0.45f
