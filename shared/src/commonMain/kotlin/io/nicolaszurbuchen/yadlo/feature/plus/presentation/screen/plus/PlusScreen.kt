package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.navigation.tabContentPadding
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SocialLinksRow
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component.PlusCard
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component.PlusSkeleton

/**
 * *Plus* — the permanent home of everything the festival is that is not its programme.
 *
 * An iOS-style grouped list, and the one screen in the app that is mostly a table of contents.
 * Accueil borrows individual entries from it by Phase, so the institutional and call-to-action
 * material surfaces in March rather than sitting unread in July; this is where all of it lives the
 * rest of the year.
 *
 * **No bar of its own.** This is a tab root, and the shell already draws one — the festival's name
 * and the edition's dates, on every root. A second bar underneath saying "Plus" would repeat what
 * the selected tab in the bottom bar has just said, and cost a fifth of the screen to do it.
 *
 * The networks close the screen rather than opening one of their own. They are how a page signs
 * off, the same way Accueil ends; four links out never justified a row that led to a screen of four
 * links out.
 */
@Composable
fun PlusScreen(
    state: PlusUiModel,
    onEntryClick: (PlusEntryUiModel) -> Unit,
    onSocialClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        // The shell's bars are drawn over this list rather than beside it, so their height is part
        // of the padding — see tabContentPadding.
        contentPadding =
            tabContentPadding(
                start = MaterialTheme.spacing.md,
                top = MaterialTheme.spacing.md,
                end = MaterialTheme.spacing.md,
                bottom = MaterialTheme.spacing.xl,
            ),
        modifier = modifier.fillMaxSize(),
    ) {
        if (state.isLoading) {
            // One item rather than a skeleton per group: the placeholder is a single animation, and
            // splitting it across lazy items would let the pulse fall out of step as they scroll in.
            item(key = SKELETON_KEY) { PlusSkeleton() }
        } else {
            items(state.groups, key = { it.id.name }) { group ->
                PlusCard(group = group, onEntryClick = onEntryClick)
            }

            if (state.socials.isNotEmpty()) {
                item(key = SOCIALS_KEY) {
                    SocialLinksRow(items = state.socials, onSocialClick = onSocialClick)
                }
            }
        }
    }
}

private const val SKELETON_KEY = "skeleton"
private const val SOCIALS_KEY = "socials"
