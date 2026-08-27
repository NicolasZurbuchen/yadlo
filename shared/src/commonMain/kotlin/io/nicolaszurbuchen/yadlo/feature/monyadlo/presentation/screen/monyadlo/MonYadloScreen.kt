package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.navigation.LocalTabChromeInsets
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.SlotScaleRow
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.CHEVRON_SIZE
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.MonYadloSkeleton
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.PlannedDayBlock
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.RAIL_WIDTH
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.WishlistTile
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The tab: one hero for *à essayer*, then the Plan as a rail of days, under the span they are all
 * measured against.
 *
 * **Recall only** — DECISIONS.md § Mon Yadlo never browses. There is no add-flow and no search here;
 * a row opens the fiche it came from, which is also the one place it can be taken off the Plan
 * again, so the same thing never carries two hearts.
 *
 * **The scale is chrome, and it is inset to where the bars actually begin.** It sits on the app
 * bar's own blue and continues it, as the Programme's filter block does, because it is not part of
 * any one day — one axis covers all three, which is the only arrangement under which a single scale
 * above three days is telling the truth. The left inset is the date rail plus the gap after it, the
 * right one is the chevron column: a scale offset from the axis it labels does not fail to answer
 * the question, it answers it wrongly.
 *
 * The hero scrolls and the scale does not, which is the right way round. *À essayer* is a
 * destination you tap once; the scale is a legend for what you are reading, and a legend that
 * scrolls away is a legend you have to scroll back for.
 */
@Composable
fun MonYadloScreen(
    state: MonYadloUiModel,
    onSlotClick: (String) -> Unit,
    onWishlistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        MonYadloSkeleton(modifier = modifier)
        return
    }

    // The shell's bars are drawn over this screen rather than beside it. The chrome clears the top
    // one here, and the list clears the bottom one in its own content padding.
    val chrome = LocalTabChromeInsets.current

    Column(modifier = modifier.fillMaxSize().padding(top = chrome.top)) {
        state.scale?.let { scale ->
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.appColors.primarySubtle)
                        .padding(bottom = MaterialTheme.spacing.xs),
            ) {
                SlotScaleRow(
                    scale = scale,
                    modifier =
                        Modifier.padding(
                            // The rail and the gap after it, which is where every bar on this
                            // screen starts — see PlannedDayBlock.
                            start = MaterialTheme.spacing.md + RAIL_WIDTH + MaterialTheme.spacing.md,
                            // And the chevron column, where every bar stops — see PlannedSlotRow.
                            end = MaterialTheme.spacing.sm + CHEVRON_SIZE + MaterialTheme.spacing.sm,
                        ),
                )
            }
        }

        LazyColumn(
            // No arrangement of its own: the gaps here are not all the same size, and the rules
            // between days have to sit in the middle of theirs rather than beside them.
            contentPadding =
                PaddingValues(
                    top = MaterialTheme.spacing.md,
                    bottom = MaterialTheme.spacing.md + chrome.bottom,
                ),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "wishlist") {
                WishlistTile(
                    count = state.wishlistCount,
                    onClick = onWishlistClick,
                    modifier =
                        Modifier.padding(
                            start = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.md,
                            bottom = MaterialTheme.spacing.lg,
                        ),
                )
            }

            state.emptyMessage?.let { message ->
                item(key = "empty") {
                    Text(
                        text = message.asString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.appColors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.xxl),
                    )
                }
            }

            itemsIndexed(state.days, key = { _, day -> day.id }) { index, day ->
                // A rule between days and never around one of them, as between two Slots on the
                // Programme. Inset to the width of the block above it rather than run to the screen
                // edges: everything on this screen is a block held off the margin, and a rule that
                // reaches further than the things it separates reads as a divider of the page.
                if (index > 0) {
                    HorizontalDivider(
                        color = MaterialTheme.appColors.borderSubtle,
                        modifier =
                            Modifier.padding(
                                horizontal = MaterialTheme.spacing.md,
                                vertical = MaterialTheme.spacing.md,
                            ),
                    )
                }

                PlannedDayBlock(day = day, onRowClick = onSlotClick)
            }
        }
    }
}
