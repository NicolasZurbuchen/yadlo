package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.infra.text.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.faq_title

/**
 * *Questions fréquentes.*
 *
 * Each question is a section header and its answer is the body under it — the same shape every
 * other Plus page uses, rather than an accordion. Nothing is hidden behind a tap: there are four
 * questions today, and the screen exists because none of them had a home at all. Three of the four
 * were answered publicly, once, in an Instagram caption that has since scrolled away.
 */
@Composable
fun FaqScreen(
    state: FaqUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.faq_title),
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

        state.entries.forEach { entry ->
            PlusSection(title = entry.question) {
                PlusBodyText(text = entry.answer)
            }
        }
    }
}
