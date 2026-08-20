package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * The carte's own navigation: one tab per menu group, then one for *Liens*, pinned under the bar
 * once the first group has gone under it.
 *
 * **The pattern is deliberately the one every food app uses.** A stand's fiche is five groups and
 * fourteen dishes, and the question a reader arrives with is "what are the drinks" or "what does a
 * bokit cost" — not "read this from the top". Tabs that scroll to a heading, and a selection that
 * follows the scroll back, is a control nobody has to be taught.
 *
 * **It appears rather than living at the top.** At rest the fiche is a photograph and a name; the
 * tabs arrive at the moment the first group would slide under the toolbar, which is exactly when
 * they become the only way to see where you are. Tying them to the Happening rather than to the
 * scroll would put a row of controls over the picture on a screen that has not been touched.
 *
 * **The selected tab is marked by its rule, not by its colour.** The obvious alternative — full ink
 * when selected, faded when not — cannot be done here: the tabs sit on the Category's own fill, and
 * on the dark theme's neutral slate the ink clears 4.96:1 at full strength. Any alpha at all puts
 * every unselected label under the 4.5:1 floor, and these are labels a reader has to read to use
 * the control. So every label is full ink and the 3dp rule under one of them is what says which. It
 * is the same rule, in the same colour, that closes the header above — see [HappeningHeader].
 *
 * The row scrolls itself to the selected tab, which is what makes the follow-the-scroll behaviour
 * work at all: a stand with five groups does not fit them on one screen, and a selection you cannot
 * see is not a selection.
 */
@Composable
fun HappeningMenuTabs(
    labels: List<String>,
    selectedIndex: Int,
    fill: Color,
    ink: Color,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowState = rememberLazyListState()

    LaunchedEffect(selectedIndex, labels.size) {
        if (selectedIndex in labels.indices) {
            rowState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = rowState,
        // Inside the scroll rather than around it, so the first and last tab can reach the screen
        // edges instead of stopping short of them — see StandMarkChips for the same choice.
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth().background(fill),
    ) {
        itemsIndexed(items = labels) { index, label ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .clickable { onTabClick(index) }
                        .padding(horizontal = MaterialTheme.spacing.sm),
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier.padding(
                            top = MaterialTheme.spacing.md,
                            bottom = MaterialTheme.spacing.sm,
                        ),
                )

                // Drawn on every tab and transparent on all but one, so choosing a different group
                // never changes the height of the bar it is chosen from.
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(INDICATOR_HEIGHT)
                            .background(if (index == selectedIndex) ink else Color.Transparent),
                )
            }
        }
    }
}

/** The header's own rule, to the point: the same three that close the photograph above. */
private val INDICATOR_HEIGHT = 3.dp
