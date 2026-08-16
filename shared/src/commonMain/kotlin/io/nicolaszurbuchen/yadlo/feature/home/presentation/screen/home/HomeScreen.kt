package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SocialLinksRow
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component.AnnouncementsBlock
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component.CountdownBlock
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component.FiguresBlock
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component.HeroBlock
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component.ThankYouBlock

/**
 * Accueil renders the block stack it is handed, in the order it is handed. Which blocks a Phase
 * gets is decided in the UiMapper; this file only knows how each one looks.
 */
@Composable
fun HomeScreen(
    state: HomeUiModel,
    onHeroClick: () -> Unit,
    onAnnouncementClick: (String) -> Unit,
    onSeeAllAnnouncementsClick: () -> Unit,
    onSocialClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        contentPadding = PaddingValues(MaterialTheme.spacing.md),
        modifier = modifier.fillMaxSize(),
    ) {
        // The block type is the identity: a Phase never produces two blocks of the same kind, and
        // keying on position would re-use a countdown's slot for a thank-you when the Phase turns.
        items(state.blocks, key = { it::class.simpleName.orEmpty() }) { block ->
            when (block) {
                is HomeBlockUiModel.Countdown -> {
                    CountdownBlock(block = block)
                }

                is HomeBlockUiModel.Hero -> {
                    HeroBlock(block = block, onClick = onHeroClick)
                }

                is HomeBlockUiModel.ThankYou -> {
                    ThankYouBlock(block = block)
                }

                is HomeBlockUiModel.Figures -> {
                    FiguresBlock(block = block)
                }

                is HomeBlockUiModel.Announcements -> {
                    AnnouncementsBlock(
                        block = block,
                        onAnnouncementClick = onAnnouncementClick,
                        onSeeAllClick = onSeeAllAnnouncementsClick,
                    )
                }

                is HomeBlockUiModel.Social -> {
                    SocialLinksRow(items = block.items, onSocialClick = onSocialClick)
                }
            }
        }
    }
}
