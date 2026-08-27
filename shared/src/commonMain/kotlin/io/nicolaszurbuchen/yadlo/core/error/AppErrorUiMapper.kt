package io.nicolaszurbuchen.yadlo.core.error

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.WifiOff
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.error_content_unreadable_subtitle
import yadlo.shared.generated.resources.error_content_unreadable_title
import yadlo.shared.generated.resources.error_database_generic_subtitle
import yadlo.shared.generated.resources.error_database_insert_failed_title
import yadlo.shared.generated.resources.error_database_query_failed_title
import yadlo.shared.generated.resources.error_network_http_subtitle_default
import yadlo.shared.generated.resources.error_network_http_title
import yadlo.shared.generated.resources.error_network_timeout_subtitle
import yadlo.shared.generated.resources.error_network_timeout_title
import yadlo.shared.generated.resources.error_network_unavailable_subtitle
import yadlo.shared.generated.resources.error_network_unavailable_title
import yadlo.shared.generated.resources.error_unexpected_subtitle
import yadlo.shared.generated.resources.error_unexpected_title

fun AppError.toUiModel(): AppErrorUiModel =
    when (this) {
        is AppError.Network.Unavailable -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_network_unavailable_title),
                subtitle = UiText.Resource(Res.string.error_network_unavailable_subtitle),
                icon = Icons.Outlined.WifiOff,
            )
        }

        is AppError.Network.Timeout -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_network_timeout_title),
                subtitle = UiText.Resource(Res.string.error_network_timeout_subtitle),
                icon = Icons.Outlined.WifiOff,
            )
        }

        is AppError.Network.Http -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_network_http_title),
                subtitle =
                    serverMessage?.let { UiText.Raw(it) }
                        ?: UiText.Resource(Res.string.error_network_http_subtitle_default),
                icon = Icons.Outlined.WifiOff,
            )
        }

        is AppError.Database.QueryFailed -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_database_query_failed_title),
                subtitle = UiText.Resource(Res.string.error_database_generic_subtitle),
                icon = Icons.Outlined.Storage,
            )
        }

        is AppError.Database.InsertFailed -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_database_insert_failed_title),
                subtitle = UiText.Resource(Res.string.error_database_generic_subtitle),
                icon = Icons.Outlined.Storage,
            )
        }

        // One screen for both cases: an unresolved reference and an unreadable field are the same
        // event to a visitor — the festival published something this app cannot read — and neither
        // offers them a different action.
        is AppError.Content -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_content_unreadable_title),
                subtitle = UiText.Resource(Res.string.error_content_unreadable_subtitle),
                icon = Icons.Outlined.ErrorOutline,
            )
        }

        is AppError.Unexpected -> {
            AppErrorUiModel(
                title = UiText.Resource(Res.string.error_unexpected_title),
                subtitle = UiText.Resource(Res.string.error_unexpected_subtitle),
                icon = Icons.Outlined.ErrorOutline,
            )
        }
    }
