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
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.SlotRowUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One Slot on the day's list — layout B2.
 *
 * A row rather than a card: the Programme exists to weigh Slots against each other, which wants a
 * shared left edge and as little furniture between neighbours as possible. Cards were measured at
 * +32% vertical space on the Saturday and separate exactly what this screen is for comparing.
 *
 * Past rows dim and stay — bar included. By 21:00 on the Saturday that is most of the list, and
 * that is accepted: reading what has already happened is part of reading the day you are in.
 *
 * **The bar spans the row's text, not the row.** It ran the full width, from under the Category
 * mark to under the chevron, which put the axis's zero and its end at different x's from everything
 * above them and made both look like furniture on the timeline. The mark and the chevron are columns
 * of their own now, one either side, and the axis is what is between them.
 * [ProgrammeScaleRow] carries both insets, so the three readings at the top of the list sit over the
 * positions they describe.
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

    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        // The chevron is the whole height of the row and centred in it, which is what makes it read
        // as belonging to the row rather than to the first line of it.
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(row.happeningId) }
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(
                    start = MaterialTheme.spacing.md,
                    top = ROW_VERTICAL_PADDING,
                    end = MaterialTheme.spacing.sm,
                    bottom = ROW_VERTICAL_PADDING,
                ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
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

                        // The Category written out, beside the mark that colours it. Colour is
                        // never the only carrier: in July sun, on a phone, it is the word that
                        // survives.
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

            SlotTimeBar(
                barStart = row.barStart,
                barEnd = row.barEnd,
                categoryFill = category.fill,
                state = row.state,
                modifier = Modifier.padding(start = CATEGORY_MARK_SIZE + MaterialTheme.spacing.sm),
            )
        }

        // The row already opens the fiche; the chevron says so before it is tapped. Every other list
        // in the app that leads somewhere carries one, and this was the one that did not.
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(CHEVRON_SIZE),
        )
    }
}

/** Internal because [ProgrammeScaleRow] insets by it too — the axis has to start at one x. */
internal val CATEGORY_MARK_SIZE = 10.dp

/**
 * Between `sm` and `md`, which the scale skips. Eight was too tight for a row carrying a name, a
 * pill, a time, a Category and a bar; sixteen is a third again as much scrolling on a list of fifty
 * for air nobody asked for that much of.
 */
private val ROW_VERTICAL_PADDING = 12.dp

/**
 * Material's default, unlike every other icon in the app, which is cut to its label's line height.
 * This one has no label beside it — it is a column of its own, the height of the row — and at 20dp
 * it read as something that had been left over rather than as the edge of the row.
 *
 * Internal because [ProgrammeScaleRow] insets by it too: the axis ends where this column starts.
 */
internal val CHEVRON_SIZE = 24.dp

/**
 * Half of titleMedium's 24sp line box less half the mark, so the square centres on the *first* line
 * of the name rather than floating at the top edge of one that wraps to two.
 */
private val MARK_TOP_OFFSET = 7.dp

/** The prototype's 0.42, rounded. Low enough to recede, high enough to still be read. */
private const val PAST_ALPHA = 0.45f
