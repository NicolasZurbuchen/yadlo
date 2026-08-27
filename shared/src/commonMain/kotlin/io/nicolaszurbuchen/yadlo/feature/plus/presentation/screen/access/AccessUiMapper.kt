package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFactMarkUiModel
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.access_empty

fun AccessState.toUiModel(): AccessUiModel {
    if (!hasLoaded) {
        return AccessUiModel(isLoading = true, modes = emptyList(), emptyMessage = null)
    }

    val modes = transport?.modes.orEmpty()

    return AccessUiModel(
        isLoading = false,
        modes =
            modes.map { mode ->
                AccessModeUiModel(
                    id = mode.id,
                    name = mode.name,
                    body = mode.body,
                    facts =
                        mode.facts.map {
                            AccessFactUiModel(
                                id = it.id,
                                text = it.text,
                                // No CROSS here. A transport mode that does not exist is left out of
                                // the content entirely rather than published as a refusal, so the
                                // only two states a fact can be in are stated and warned about.
                                mark = if (it.caveat) YadloFactMarkUiModel.INFO else YadloFactMarkUiModel.CHECK,
                            )
                        },
                    links =
                        mode.links.map {
                            AccessLinkUiModel(id = it.id, label = it.label, sublabel = it.sublabel, url = it.url)
                        },
                    nights =
                        mode.departures.map { departure ->
                            AccessNightUiModel(
                                id = departure.id,
                                night = departure.night,
                                // The fiche's separator again, so a list of times reads the same
                                // wherever the app writes one.
                                times = departure.times.joinToString(" · ") { it.time },
                                notes =
                                    departure.times.mapNotNull { time ->
                                        time.note?.let { "${time.time} — $it" }
                                    },
                            )
                        },
                )
            },
        emptyMessage = if (modes.isEmpty()) UiText.Resource(Res.string.access_empty) else null,
    )
}
