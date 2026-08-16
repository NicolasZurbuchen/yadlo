package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.responsible_empty

fun ResponsibleState.toUiModel(): ResponsibleUiModel {
    val loaded =
        page ?: return ResponsibleUiModel(isLoading = true, sections = emptyList(), emptyMessage = null)

    return ResponsibleUiModel(
        isLoading = false,
        sections =
            loaded.sections.map { section ->
                ResponsibleSectionUiModel(
                    id = section.id,
                    title = section.title,
                    body = section.body,
                    links =
                        section.links.map {
                            ResponsibleLinkUiModel(id = it.id, label = it.label, sublabel = it.sublabel, url = it.url)
                        },
                )
            },
        // A page whose charters were published empty rather than one still loading. The row that
        // opens it is derived from the same count, so this is the restored-back-stack case.
        emptyMessage = if (loaded.sections.isEmpty()) UiText.Resource(Res.string.responsible_empty) else null,
    )
}
