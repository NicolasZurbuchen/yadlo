package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_stands_food
import yadlo.shared.generated.resources.search_empty
import yadlo.shared.generated.resources.search_group_on_site
import yadlo.shared.generated.resources.search_group_practical
import yadlo.shared.generated.resources.search_group_programme
import yadlo.shared.generated.resources.search_hint

/**
 * Four states, and the two queries in the middle are two different shapes of answer rather than two
 * examples of one.
 *
 * **No query answers with all three groups, and the preview says so rather than inventing one that
 * does.** *vegan* is a food question: it finds stands and the screen that lists them, so *Sur place*
 * and *Pratique* appear and *Programme* is absent entirely — the mapper drops an empty group rather
 * than drawing an empty heading. *eau* is the opposite half: two activities and the question that
 * answers it. Between them every group title, both row shapes and both kinds of title are drawn.
 *
 * **[SearchRowUiModel.Happening.reason] is the thing to look at.** *Vegan Fabrik* matched on its own
 * name and carries none; *Guliko* matched on a dish and has to say why it is in a list of vegan
 * results, or it reads as a mistake. The row has to hold its shape both ways.
 */
private class SearchScreenStateProvider : PreviewParameterProvider<SearchUiModel> {
    override val values =
        sequenceOf(
            // An empty field has not asked anything, so it gets the invitation rather than a verdict.
            SearchUiModel(query = "", groups = emptyList(), message = UiText.Resource(Res.string.search_hint)),
            SearchUiModel(
                query = "vegan",
                message = null,
                groups =
                    listOf(
                        SearchGroupUiModel(
                            id = "on-site",
                            title = UiText.Resource(Res.string.search_group_on_site),
                            rows =
                                listOf(
                                    SearchRowUiModel.Happening(
                                        id = "on-site-vegan-fabrik",
                                        happeningId = "vegan-fabrik",
                                        name = "Vegan Fabrik",
                                        categoryId = "restauration",
                                        categoryName = "Restauration",
                                        reason = null,
                                    ),
                                    SearchRowUiModel.Happening(
                                        id = "on-site-guliko",
                                        happeningId = "guliko",
                                        name = "Guliko",
                                        categoryId = "restauration",
                                        categoryName = "Restauration",
                                        reason = "options véganes",
                                    ),
                                ),
                        ),
                        // A topic before any question, because a topic is a whole screen.
                        SearchGroupUiModel(
                            id = "practical",
                            title = UiText.Resource(Res.string.search_group_practical),
                            rows =
                                listOf(
                                    SearchRowUiModel.Practical(
                                        id = "topic-STANDS_FOOD",
                                        topic = SearchTopicUiModel.STANDS_FOOD,
                                        title = UiText.Resource(Res.string.plus_entry_stands_food),
                                    ),
                                ),
                        ),
                    ),
            ),
            SearchUiModel(
                query = "eau",
                message = null,
                groups =
                    listOf(
                        SearchGroupUiModel(
                            id = "programme",
                            title = UiText.Resource(Res.string.search_group_programme),
                            rows =
                                listOf(
                                    SearchRowUiModel.Happening(
                                        id = "programme-gladiasup",
                                        happeningId = "gladiasup",
                                        name = "GladiaSUP",
                                        categoryId = "eau",
                                        categoryName = "Sur l'eau",
                                        reason = "Sur l'eau",
                                    ),
                                    SearchRowUiModel.Happening(
                                        id = "programme-trampoline-flottant",
                                        happeningId = "trampoline-flottant",
                                        name = "Trampoline flottant",
                                        categoryId = "eau",
                                        categoryName = "Sur l'eau",
                                        reason = "Sur l'eau",
                                    ),
                                ),
                        ),
                        SearchGroupUiModel(
                            id = "practical",
                            title = UiText.Resource(Res.string.search_group_practical),
                            rows =
                                listOf(
                                    // Raw, because a question is the association's own words and the
                                    // row that repeats them is what says the answer is on that page.
                                    SearchRowUiModel.Practical(
                                        id = "faq-eau-potable",
                                        topic = SearchTopicUiModel.FAQ,
                                        title = UiText.Raw("Y a-t-il de l'eau potable gratuite sur le site ?"),
                                    ),
                                ),
                        ),
                    ),
            ),
            // A query that ran and found nothing, which is a fact about the festival rather than an
            // invitation — and it quotes the query back so it reads as an answer to what was asked.
            SearchUiModel(
                query = "quinoa",
                groups = emptyList(),
                message = UiText.Resource(Res.string.search_empty, listOf("quinoa")),
            ),
        )
}

@PreviewThemes
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(SearchScreenStateProvider::class) state: SearchUiModel,
) {
    YadloPreview {
        SearchScreen(
            state = state,
            onQueryChange = {},
            onHappeningClick = {},
            onTopicClick = {},
            onBackClick = {},
        )
    }
}
