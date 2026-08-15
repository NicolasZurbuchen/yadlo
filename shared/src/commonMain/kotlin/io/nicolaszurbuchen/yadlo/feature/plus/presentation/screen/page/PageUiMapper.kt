package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.page_empty

fun PageState.toUiModel(): PageUiModel {
    // The one thing the content cannot supply: the entry's own name, which is the app's word and
    // has to match the row that opened it.
    val title = UiText.Resource(kind.title)

    val loaded =
        page ?: return PageUiModel(isLoading = true, title = title, sections = emptyList(), emptyMessage = null)

    return PageUiModel(
        isLoading = false,
        title = title,
        sections =
            loaded.sections.map { section ->
                PageSectionUiModel(
                    id = section.id,
                    title = section.title,
                    body = section.body,
                    links =
                        section.links.map {
                            PageLinkUiModel(id = it.id, label = it.label, sublabel = it.sublabel, url = it.url)
                        },
                )
            },
        // A page whose section was published empty — no charters, no networks — rather than one
        // still loading. The row that opens it is derived from the same count, so this is the
        // restored-back-stack case again.
        emptyMessage = if (loaded.sections.isEmpty()) UiText.Resource(Res.string.page_empty) else null,
    )
}
