package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_responsible
import yadlo.shared.generated.resources.plus_entry_social

/**
 * The shared text page: a title, some prose, and somewhere to go.
 *
 * Most of the Plus tab is that shape, so it is one screen rather than a folder of near-identical
 * ones. [title] is the only part the content does not supply — it is the app's word for the entry,
 * and the sections under it are the association's.
 */
data class PageUiModel(
    val isLoading: Boolean,
    val title: UiText,
    val sections: List<PageSectionUiModel>,
    val emptyMessage: UiText?,
)

/**
 * Which page this is, in presentation's own terms, carrying the one string the content cannot
 * supply.
 *
 * A mirror of `PlusPageId` rather than the enum itself, and the mirror earns its keep: the store
 * translates once at construction, so the UiMapper never has to name a domain type — which it is
 * not allowed to import, and which the alternative would have smuggled in fully qualified.
 */
enum class PageKind(
    val title: StringResource,
) {
    RESPONSIBLE(Res.string.plus_entry_responsible),
    SOCIAL(Res.string.plus_entry_social),
}

data class PageSectionUiModel(
    val id: String,
    val title: String?,
    val body: String?,
    val links: List<PageLinkUiModel>,
)

data class PageLinkUiModel(
    val id: String,
    val label: String,
    val sublabel: String?,
    val url: String,
)
