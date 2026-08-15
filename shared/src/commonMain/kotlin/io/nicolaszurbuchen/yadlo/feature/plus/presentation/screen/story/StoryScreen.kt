package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.component.FigureRow
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
        modifier = modifier,
    ) {
        state.emptyMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        state.body?.let { PlusBodyText(text = it) }

        if (state.passageTitle != null && state.passageBody != null) {
            PlusSection(title = state.passageTitle) {
                PlusBodyText(text = state.passageBody)
            }
        }

        if (state.figures.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.home_figures_title)) {
                FigureRow(figures = state.figures)

                state.figuresCaveat?.let {
                    Text(
                        text = it.asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.appColors.textTertiary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
