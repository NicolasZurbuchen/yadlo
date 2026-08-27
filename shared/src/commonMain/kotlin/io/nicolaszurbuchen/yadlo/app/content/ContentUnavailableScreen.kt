package io.nicolaszurbuchen.yadlo.app.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.core.error.AppErrorUiModel
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.error_retry
import yadlo.shared.generated.resources.startup_content_unavailable_body

/**
 * The screen story 68 is about: a first launch with no signal, told plainly rather than left on a
 * spinner that never resolves. Reached only when there is no cache at all — once one fetch has ever
 * succeeded, a failed refresh keeps showing the cached programme and this is never seen.
 */
@Composable
fun ContentUnavailableScreen(
    error: AppErrorUiModel,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md, Alignment.CenterVertically),
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.appColors.background)
                .safeDrawingPadding()
                .padding(MaterialTheme.spacing.xl),
    ) {
        Icon(
            imageVector = error.icon,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(ICON_SIZE),
        )

        Text(
            text = error.title.asString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.textPrimary,
            textAlign = TextAlign.Center,
        )

        // What went wrong sits above; this says what it means for the visitor, which is the part
        // story 68 is actually about — one connection now, and then it works on the beach.
        Text(
            text = stringResource(Res.string.startup_content_unavailable_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textSecondary,
            textAlign = TextAlign.Center,
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = MaterialTheme.spacing.md),
        ) {
            Text(text = stringResource(Res.string.error_retry))
        }
    }
}

private val ICON_SIZE = 48.dp
