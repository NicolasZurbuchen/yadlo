package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
 * It stays on the card ground rather than taking the bar's blue, which the Programme's filter block
 * does take. The dietary chips carry six measured tints and three of them fall under 3:1 on that
 * blue — a colour system already spent on the dish tags and the stand rows is not worth re-picking
 * for one strip of chips, and the tinted card is a ground those tints were measured against.
 */
@Composable
fun PlusScreenScaffold(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    underBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.appColors.surface)) {
                YadloTopAppBar(title = title, onBackClick = onBackClick)

                underBar()
            }
        },
        modifier = modifier,
        content = content,
    )
}
