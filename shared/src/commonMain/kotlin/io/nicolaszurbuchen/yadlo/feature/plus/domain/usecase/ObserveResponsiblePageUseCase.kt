package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ResponsiblePage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/** A charter's own name is its label; there is nothing to add under it. */
private val NO_SUBLABEL: String? = null

/**
 * *Festival responsable* — the charters the association has signed.
 *
 * One section per charter, because each is a separate commitment with its own body and its own site.
 * A single charter is the common case and reads as an ordinary page.
 *
 * This used to take a page id and branch on it, back when *Réseaux sociaux* was a second page
 * sharing the same screen. The networks became the foot of the tab, the enum was left with one
 * value, and a parameter with one possible argument is a decision nobody makes.
 */
class ObserveResponsiblePageUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<ResponsiblePage> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                ResponsiblePage(
                    sections =
                        status.bundle.festival.charters.map { charter ->
                            ResponsiblePage.Section(
                                id = charter.id,
                                title = charter.name,
                                body = charter.body,
                                links =
                                    listOfNotNull(
                                        charter.url?.let {
                                            InfoLink(
                                                id = charter.id,
                                                label = charter.name,
                                                sublabel = NO_SUBLABEL,
                                                url = it,
                                            )
                                        },
                                    ),
                            )
                        },
                )
            }
}
