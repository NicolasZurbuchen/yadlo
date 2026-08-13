package io.nicolaszurbuchen.yadlo.app.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppErrorUiModel
import io.nicolaszurbuchen.yadlo.common.error.toUiModel

/**
 * Both errors that can actually land here, built through the real [toUiModel] mapping rather than
 * hand-written models — a preview that invents its own strings stops tracking the screen it previews.
 */
private class ContentUnavailableErrorProvider : PreviewParameterProvider<AppErrorUiModel> {
    override val values =
        sequenceOf(
            AppError.Network.Unavailable.toUiModel(),
            AppError.Content.MalformedField(field = "cache", detail = "unparseable").toUiModel(),
        )
}

@Preview
@Composable
private fun ContentUnavailableScreenLightPreview(
    @PreviewParameter(ContentUnavailableErrorProvider::class) error: AppErrorUiModel,
) {
    YadloTheme(darkTheme = false) {
        ContentUnavailableScreen(error = error, onRetry = {})
    }
}

@Preview
@Composable
private fun ContentUnavailableScreenDarkPreview(
    @PreviewParameter(ContentUnavailableErrorProvider::class) error: AppErrorUiModel,
) {
    YadloTheme(darkTheme = true) {
        ContentUnavailableScreen(error = error, onRetry = {})
    }
}
