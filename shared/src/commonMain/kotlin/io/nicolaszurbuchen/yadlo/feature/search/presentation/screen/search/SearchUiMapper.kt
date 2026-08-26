package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.mapper.toUiModel
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.search_empty
import yadlo.shared.generated.resources.search_group_on_site
import yadlo.shared.generated.resources.search_group_practical
import yadlo.shared.generated.resources.search_group_programme
import yadlo.shared.generated.resources.search_hint

/**
 * One query, three groups, and the two ways of having nothing to show.
 *
 * **The order of the groups is the order of the questions.** What is on, then what is on site, then
 * how the weekend works — which is also the order the tabs are in, so a reader scanning results is
 * scanning the app they already know.
 *
 * **Topics come before the FAQ questions inside the practical group**, because a topic is a whole
 * screen and a question is one line on one of them. `twint` should land on *Paiement* rather than on
 * a question that happens to mention it.
 *
 * Everything is built in this one function: a UiMapper file may hold nothing but the
 * State-to-UiModel extension, so a helper here would have to be local, which Konsist reads as an
 * extra function in the file.
 */
fun SearchState.toUiModel(): SearchUiModel {
    val programmeRows =
        results?.programme.orEmpty().map { hit ->
            SearchRowUiModel.Happening(
                // Prefixed, because the same Happening cannot appear in two groups but a key has to
                // be unique across the flattened list the LazyColumn actually draws.
                id = "programme-${hit.happening.id}",
                happeningId = hit.happening.id,
                name = hit.happening.name,
                categoryId = hit.happening.category.id,
                categoryName = hit.happening.category.name,
                reason = hit.reason,
            )
        }

    val onSiteRows =
        results?.onSite.orEmpty().map { hit ->
            SearchRowUiModel.Happening(
                id = "on-site-${hit.happening.id}",
                happeningId = hit.happening.id,
                name = hit.happening.name,
                categoryId = hit.happening.category.id,
                categoryName = hit.happening.category.name,
                reason = hit.reason,
            )
        }

    val practicalRows =
        results?.topics.orEmpty().map { topic ->
            val matched = topic.toUiModel()

            SearchRowUiModel.Practical(
                id = "topic-${matched.name}",
                topic = matched,
                title = UiText.Resource(matched.title),
            )
        } +
            results?.faq.orEmpty().map { entry ->
                SearchRowUiModel.Practical(
                    id = "faq-${entry.id}",
                    // Every question opens the one screen that answers it. Raw rather than a
                    // resource: the question is the association's own words, and the row that
                    // repeats them back is what tells the reader their answer is on that page.
                    topic = SearchTopicUiModel.FAQ,
                    title = UiText.Raw(entry.question),
                )
            }

    val groups =
        listOfNotNull(
            programmeRows
                .takeIf { it.isNotEmpty() }
                ?.let {
                    SearchGroupUiModel(
                        id = "programme",
                        title = UiText.Resource(Res.string.search_group_programme),
                        rows = it,
                    )
                },
            onSiteRows
                .takeIf { it.isNotEmpty() }
                ?.let {
                    SearchGroupUiModel(
                        id = "on-site",
                        title = UiText.Resource(Res.string.search_group_on_site),
                        rows = it,
                    )
                },
            practicalRows
                .takeIf { it.isNotEmpty() }
                ?.let {
                    SearchGroupUiModel(
                        id = "practical",
                        title = UiText.Resource(Res.string.search_group_practical),
                        rows = it,
                    )
                },
        )

    val trimmed = query.trim()

    return SearchUiModel(
        query = query,
        groups = groups,
        // An empty field has not asked anything, so it gets the invitation rather than a verdict.
        // Every query that is not empty has genuinely been run, which is what lets the other message
        // say "aucun résultat" without qualifying it.
        message =
            when {
                groups.isNotEmpty() -> null
                trimmed.isEmpty() -> UiText.Resource(Res.string.search_hint)
                else -> UiText.Resource(Res.string.search_empty, listOf(trimmed))
            },
    )
}
