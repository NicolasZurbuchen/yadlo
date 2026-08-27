package io.nicolaszurbuchen.yadlo.core.error

import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

data class AppErrorUiModel(
    val title: UiText,
    val subtitle: UiText,
    val icon: ImageVector,
)
