package io.nicolaszurbuchen.yadlo.app.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppErrorUiModel
import io.nicolaszurbuchen.yadlo.common.error.toUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * Both errors that can actually land here, built through the real [toUiModel] mapping rather than
 * hand-written models — a preview that invents its own strings stops tracking the screen it previews.
 */
private class ContentUnavailableScreenStateProvider : PreviewParameterProvider<AppErrorUiModel> {
    override val values =
        sequenceOf(
            AppError.Network.Unavailable.toUiModel(),
            AppError.Content.MalformedField(field = "cache", detail = "unparseable").toUiModel(),
        )
}

@PreviewThemes
@Composable
private fun ContentUnavailableScreenPreview(
    @PreviewParameter(ContentUnavailableScreenStateProvider::class) error: AppErrorUiModel,
) {
    YadloPreview {
        ContentUnavailableScreen(error = error, onRetry = {})
    }
}
