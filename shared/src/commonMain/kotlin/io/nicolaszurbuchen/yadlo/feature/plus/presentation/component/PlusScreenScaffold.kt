package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_back

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
 */
@OptIn(ExperimentalMaterial3Api::class)
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
            // The same ground as the bar above it, so the two read as one surface rather than as a
            // bar with a strip stuck under it.
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.appColors.surface)) {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            // Set explicitly: TopAppBar defaults its title to titleLarge, which in
                            // this project is the button-label role rather than a heading.
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.plus_back),
                            )
                        }
                    },
                )

                underBar()
            }
        },
        modifier = modifier,
        content = content,
    )
}
