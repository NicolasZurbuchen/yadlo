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
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.SlotTimeBar
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.SlotRowUiModel
import io.nicolaszurbuchen.yadlo.infra.text.asString

/**
 * One Slot on the day's list — layout B2.
 *
 * A row rather than a card: the Programme exists to weigh Slots against each other, which wants a
 * shared left edge and as little furniture between neighbours as possible. Cards were measured at
 * +32% vertical space on the Saturday and separate exactly what this screen is for comparing.
 *
 * **Three lines, and a right-hand column down two of them.** The Category names the first line and
 * the live state answers it on the right; the name has the second, with what it costs on the right
 * of that; the time has the third to itself. Everything on the left is what the thing *is* and
 * everything on the right is what to do about it, which is the reading order someone scanning a
 * Saturday actually uses.
 *
 * It was one line with the name, the pill and the price competing for it. That holds for `AMC` and
 * breaks for a five-word artist name: the price gets pushed off the end of the row or the pill is
 * squeezed into a column two characters wide, and neither failure shows until the content is real.
 * Both are weighted against their own line now, so a long name takes a second line and the price
 * stays where it was.
 *
 * The Category label is text-coloured rather than tinted the way it is on a fiche. The dot beside it
 * is already the colour, and the fills were chosen to sit beside each other rather than to be
 * written with — `enfants` gold measures 1.9:1 on the light page.
 *
 * **The third line holds every hour the Happening runs that day**, separated by middots, and each
 * one dims on its own once it is past — *14:00 – 15:00 · 16:00 – 17:00 · 18:00 – 19:00* with the
 * first of the three greyed at half past three. A row whose 14:00 has gone is not a row that has
 * gone, which is the distinction three separate rows could not make. A FlowRow rather than a Row,
 * because three ranges do not fit on one line at the largest text sizes and wrapping is the right
 * way for that to fail.
 *
 * **The bar dims the same hour at the same moment**, which it did not: the 14:00 greyed out in the
 * text while the mark standing for it kept full colour, so the row answered "which of these has
 * gone" one way in words and another way in the picture — and the picture is the half that is read
 * at a glance.
 *
 * Past rows dim and stay — bar included — and a row is past only when every hour on it is. By 21:00
 * on the Saturday that is most of the list, and that is accepted: reading what has already happened
 * is part of reading the day you are in.
 *
 * **The bar spans the row's text, not the row.** It ran the full width, under the chevron included,
 * which put the axis's end at a different x from everything above it. The chevron is a column of its
 * own now and the axis stops where it starts; on the left there is nothing to clear any more, since
 * the Category mark moved onto the first line and the text runs to the row's own edge.
 * [ProgrammeHeader] pads its scale by the same numbers, so the three readings at the top of the
 * list sit over the positions they describe.
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(CATEGORY_MARK_SIZE)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(category.fill),
                )

                // The Category written out beside the mark that colours it. Colour is never the
                // only carrier: in July sun, on a phone, it is the word that survives.
                Text(
                    text = row.categoryName.uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.appColors.textTertiary,
                    modifier = Modifier.weight(1f),
                )

                row.stateLabel?.let { label ->
                    SlotStatePill(label = label, state = row.state)
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

            FlowRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                row.slots.forEachIndexed { index, slot ->
                    if (index > 0) {
                        // Tertiary rather than the times' own colour, so the separator recedes
                        // behind what it separates instead of counting as a fourth reading.
                        Text(
                            text = TIME_SEPARATOR,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.appColors.textTertiary,
                        )
                    }

                    Text(
                        text = slot.timeText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.appColors.textSecondary,
                        // The row's own dimming, applied to one hour of it. Same value, so an hour
                        // that has passed on a live row looks exactly as past as a row that has.
                        modifier =
                            Modifier.alpha(
                                if (slot.state is SlotLiveStateUiModel.Over && !isOver) PAST_ALPHA else 1f,
                            ),
                    )
                }
            }

            // Per segment, and only while the row itself is not already dimmed — the same rule the
            // times above follow, so a bar and the line over it never disagree about which of the
            // three hours has gone.
            SlotTimeBar(segments = row.slots, categoryFill = category.fill, dimsPastSegments = !isOver)
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

/**
 * A middot with the spacing around it coming from the row rather than from the string, so the times
 * either side wrap independently of it.
 */
private const val TIME_SEPARATOR = "·"

private val CATEGORY_MARK_SIZE = 10.dp

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

/** The prototype's 0.42, rounded. Low enough to recede, high enough to still be read. */
private const val PAST_ALPHA = 0.45f
