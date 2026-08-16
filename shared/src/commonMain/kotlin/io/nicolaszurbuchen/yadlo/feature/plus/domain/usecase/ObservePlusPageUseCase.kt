package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPage
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusPageId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/** A charter's own name is its label; there is nothing to add under it. */
private val NO_SUBLABEL: String? = null

/**
 * One of the shared text pages, assembled from whichever part of the live-truth file it is about.
 *
 * The `when` is the whole use case, and it is deliberately the only place the app decides what a
 * page id means. Adding a page is a branch here and a title string — not a screen, a store and a
 * navigator method.
 */
class ObservePlusPageUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(pageId: PlusPageId): Flow<PlusPage> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                when (pageId) {
                    PlusPageId.RESPONSIBLE -> status.bundle.festival.toResponsiblePage()
                }
            }

    /**
     * One section per charter, because each is a separate commitment with its own body and its own
     * site. A single charter is the common case and reads as an ordinary page.
     */
    private fun Festival.toResponsiblePage() =
        PlusPage(
            sections =
                charters.map { charter ->
                    PlusPage.Section(
                        id = charter.id,
                        title = charter.name,
                        body = charter.body,
                        links =
                            listOfNotNull(
                                charter.url?.let {
                                    InfoLink(id = charter.id, label = charter.name, sublabel = NO_SUBLABEL, url = it)
                                },
                            ),
                    )
                },
        )
}
