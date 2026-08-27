package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import io.nicolaszurbuchen.yadlo.infra.text.asString

/**
 * What the list says when it has nothing — a filter that matched nothing, or a programme that has
 * not been published yet.
 *
 * One statement and no illustration. Story 64 is about never claiming a programme exists when it
 * does not, and the honest version of that is a sentence, not a graphic apologising for the
 * association's timetable.
 */
@Composable
fun ProgrammeEmptyMessage(
    message: UiText,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.xl),
    ) {
        Text(
            text = message.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
