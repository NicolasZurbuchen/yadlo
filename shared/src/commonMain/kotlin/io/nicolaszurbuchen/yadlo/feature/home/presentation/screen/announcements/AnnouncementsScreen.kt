package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.home.presentation.component.AnnouncementCard
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.announcements_back
import yadlo.shared.generated.resources.announcements_empty
import yadlo.shared.generated.resources.announcements_title

/**
 * The full feed, reached from the Accueil block. Its own screen rather than an expanding block,
 * because the block is a summary and the record has to stay readable when there are forty of them.
 *
 * Full-screen with a back chevron, so the tab bar hides the way it does for a fiche.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    state: AnnouncementsUiModel,
    onBackClick: () -> Unit,
    onAnnouncementClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.announcements_title),
                        // Set explicitly: TopAppBar defaults its title to titleLarge, and in this
                        // project that slot is the button-label role rather than a heading.
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.announcements_back),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        when {
            state.isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(contentPadding),
                ) {
                    CircularProgressIndicator()
                }
            }

            state.items.isEmpty() -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(contentPadding).padding(MaterialTheme.spacing.xl),
                ) {
                    Text(
                        text = stringResource(Res.string.announcements_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }

            else -> {
                LazyColumn(
                    // Vertical only: the rows reach the screen edges so their ripple and the rules
                    // between them do too, and each row carries its own horizontal inset.
                    contentPadding = PaddingValues(vertical = MaterialTheme.spacing.md),
                    modifier = Modifier.fillMaxSize().padding(contentPadding),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        AnnouncementCard(item = item, onClick = onAnnouncementClick)
                        HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                    }
                }
            }
        }
    }
}
