package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloTopAppBar
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/**
 * The chrome every screen behind a Plus row wears: a title and a way back. Nothing about the body.
 *
 * Split out of [PlusDetailScaffold] because most of those screens are prose and scroll as one
 * column, but not all of them: the stand lists are long, keyed and lazy, and were wearing a
 * hand-copied version of this bar rather than the shared one. The frame is the part with no
 * decisions left in it, so it is the part worth sharing; how the body scrolls is a real difference
 * between a payment page and a list of forty stands, and stays with the screen.
 *
 * [underBar] belongs to the bar, not to the content. Filter chips go here when the list underneath
 * does *not* scroll beneath them — a filter you have to scroll back up to reach is a filter that
 * gets used once, and putting it in the list means the reason someone opened the screen leaves it
 * the moment they start reading.
 *
 * It sits on the bar's own blue, continuing it, the same way the Programme's filter block does, and
 * each chip draws its edge in the ink that blue carries — see
 * [io.nicolaszurbuchen.yadlo.app.design.component.YadloFilterChip] for what that leaves the glyphs
 * inside them measuring.
 */
@Composable
fun PlusScreenScaffold(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    underBar: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.appColors.primarySubtle)) {
                YadloTopAppBar(title = title, onBackClick = onBackClick, actions = actions)

                underBar()
            }
        },
        modifier = modifier,
        content = content,
    )
}
