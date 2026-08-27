package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.component.YadloFigureGrid
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.component.StorySkeleton
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_figures_title
import yadlo.shared.generated.resources.plus_entry_story

/**
 * *L'histoire de Yadlo* — a group of windsurfers, 2015, and what it became.
 *
 * **The figures live here rather than in an entry of their own.** Three numbers do not carry a row
 * on the tab, and out of context they are trivia; under the story of how the festival started they
 * are the point. It is also the one place where the live-truth file and one frozen Edition are read
 * onto the same screen, which the domain joins so this never has to know.
 *
 * The skeleton is its own for the figures' sake — see [StorySkeleton].
 */
@Composable
fun StoryScreen(
    state: StoryUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_story),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        skeleton = { StorySkeleton() },
        modifier = modifier,
    ) {
        state.body?.let { PlusBodyText(text = it) }

        if (state.passageTitle != null && state.passageBody != null) {
            PlusSection(title = state.passageTitle) {
                PlusBodyText(text = state.passageBody)
            }
        }

        if (state.figures.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.home_figures_title)) {
                YadloFigureGrid(figures = state.figures, caveat = state.figuresCaveat?.asString())
            }
        }
    }
}
