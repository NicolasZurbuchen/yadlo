package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.component.PartnerTierBlock
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.partners_intro
import yadlo.shared.generated.resources.plus_entry_partners

/**
 * *Partenaires* — the 39 companies without which there is no festival.
 *
 * **A partner without a website says so.** Five of the thirty-nine have none, so silence on tap
 * would be the common case rather than the edge one, and a tap that does nothing reads as a bug.
 * The message is keyed on a counter in the state, so tapping twice says it twice.
 *
 * The snackbar host lives here rather than being hoisted, because a `SnackbarHostState` is neither
 * a UiModel nor a lambda and the screen boundary only carries those two.
 */
@Composable
fun PartnersScreen(
    state: PartnersUiModel,
    onBackClick: () -> Unit,
    onPartnerClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val notice = state.noWebsiteNotice
    val message = notice?.message?.asString()

    LaunchedEffect(notice?.token) {
        if (notice != null && message != null) {
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        PlusDetailScaffold(
            title = stringResource(Res.string.plus_entry_partners),
            onBackClick = onBackClick,
            isLoading = state.isLoading,
        ) {
            state.emptyMessage?.let {
                Text(
                    text = it.asString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.tiers.isNotEmpty()) {
                PlusBodyText(text = stringResource(Res.string.partners_intro))
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.tiers.forEachIndexed { index, tier ->
                    PartnerTierBlock(
                        tier = tier,
                        columns = if (index < PROMINENT_TIERS) PROMINENT_COLUMNS else COLUMNS,
                        onPartnerClick = onPartnerClick,
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(MaterialTheme.spacing.md),
        )
    }
}

/**
 * How many tiers are drawn two across before the rest go three.
 *
 * Three, which in the 2026 content is *Sponsors généraux*, *Sponsors cygnes d'or* and *Sponsors
 * cygnes d'argent*. Counted from the top rather than matched by id, because the thing being said is
 * "the top of the hierarchy gets more room" rather than "these three slugs are special" — the tiers
 * arrive already ordered by the rank the sponsors paid into, and a tier that gets renamed or a
 * fourth that gets added should not silently lose or gain a column.
 */
private const val PROMINENT_TIERS = 3

private const val PROMINENT_COLUMNS = 2

private const val COLUMNS = 3
