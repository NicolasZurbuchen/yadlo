package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_back

/**
 * The frame every screen behind a Plus row wears: a title, a way back, and one scrolling column.
 *
 * Shared rather than repeated because there are a dozen of these and the frame is the part with no
 * decisions left in it. What each screen still owns is everything inside — a payment screen and a
 * timetable have nothing else in common, which is why this stops at the scroll container rather
 * than trying to be a page template as well.
 *
 * [isLoading] is the same content-arriving state every Plus screen has, since all of them read one
 * bundle that lands once at launch.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlusDetailScaffold(
    title: String,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        // Set explicitly: TopAppBar defaults its title to titleLarge, which in this
                        // project is the button-label role rather than a heading.
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
        },
        modifier = modifier,
    ) { contentPadding ->
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(contentPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.md,
                            top = MaterialTheme.spacing.md,
                            bottom = MaterialTheme.spacing.xl,
                        ),
                content = content,
            )
        }
    }
}
