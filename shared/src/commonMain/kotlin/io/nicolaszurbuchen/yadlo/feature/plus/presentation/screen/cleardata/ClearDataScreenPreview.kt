package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.clear_data_images_action
import yadlo.shared.generated.resources.clear_data_images_body
import yadlo.shared.generated.resources.clear_data_images_empty
import yadlo.shared.generated.resources.clear_data_images_megabytes
import yadlo.shared.generated.resources.clear_data_images_title
import yadlo.shared.generated.resources.clear_data_saved_action
import yadlo.shared.generated.resources.clear_data_saved_body
import yadlo.shared.generated.resources.clear_data_saved_empty
import yadlo.shared.generated.resources.clear_data_saved_slots_other
import yadlo.shared.generated.resources.clear_data_saved_stands_other
import yadlo.shared.generated.resources.clear_data_saved_title

/**
 * The three readings worth looking at: something kept, nothing kept, and the question open over the
 * first of them.
 *
 * Written as UiModels rather than run through the mapper, unlike the previews of screens whose
 * interesting states are combinations — a preview here may not import a domain type, and the
 * arithmetic those states would exercise is held by `ClearDataUiMapperTest` instead.
 */
private class ClearDataStateProvider : PreviewParameterProvider<ClearDataUiModel> {
    override val values =
        sequenceOf(
            populated(isConfirming = false),
            // Both buttons disabled — a fresh install, and the state this screen is most likely to
            // be opened in by somebody checking what the app keeps before trusting it.
            ClearDataUiModel(
                isLoading = false,
                saved = savedRow(detail = UiText.Resource(Res.string.clear_data_saved_empty), isEnabled = false),
                images = imagesRow(detail = UiText.Resource(Res.string.clear_data_images_empty), isEnabled = false),
                isConfirmingSaved = false,
            ),
            populated(isConfirming = true),
        )

    private fun populated(isConfirming: Boolean) =
        ClearDataUiModel(
            isLoading = false,
            saved =
                savedRow(
                    detail =
                        UiText.Composite(
                            listOf(
                                UiText.Resource(Res.string.clear_data_saved_slots_other, listOf(7)),
                                UiText.Raw(" · "),
                                UiText.Resource(Res.string.clear_data_saved_stands_other, listOf(2)),
                            ),
                        ),
                    isEnabled = true,
                ),
            images =
                imagesRow(
                    detail = UiText.Resource(Res.string.clear_data_images_megabytes, listOf("4,5")),
                    isEnabled = true,
                ),
            isConfirmingSaved = isConfirming,
        )

    private fun savedRow(
        detail: UiText,
        isEnabled: Boolean,
    ) = ClearDataRowUiModel(
        title = UiText.Resource(Res.string.clear_data_saved_title),
        body = UiText.Resource(Res.string.clear_data_saved_body),
        detail = detail,
        action = UiText.Resource(Res.string.clear_data_saved_action),
        isEnabled = isEnabled,
    )

    private fun imagesRow(
        detail: UiText,
        isEnabled: Boolean,
    ) = ClearDataRowUiModel(
        title = UiText.Resource(Res.string.clear_data_images_title),
        body = UiText.Resource(Res.string.clear_data_images_body),
        detail = detail,
        action = UiText.Resource(Res.string.clear_data_images_action),
        isEnabled = isEnabled,
    )
}

@Preview
@Composable
private fun ClearDataScreenPreview(
    @PreviewParameter(ClearDataStateProvider::class) state: ClearDataUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            ClearDataScreen(
                state = state,
                onBackClick = {},
                onSavedClick = {},
                onSavedConfirm = {},
                onSavedDismiss = {},
                onImagesClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun ClearDataScreenDarkPreview(
    @PreviewParameter(ClearDataStateProvider::class) state: ClearDataUiModel,
) {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            ClearDataScreen(
                state = state,
                onBackClick = {},
                onSavedClick = {},
                onSavedConfirm = {},
                onSavedDismiss = {},
                onImagesClick = {},
            )
        }
    }
}
