package io.nicolaszurbuchen.yadlo.common.plan.data.repository

import io.nicolaszurbuchen.yadlo.common.plan.data.datasource.local.PlanLocalDataSource
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlanRepositoryImpl(
    private val localDataSource: PlanLocalDataSource,
) : PlanRepository {
    override fun observeSaved(): Flow<List<SavedItem>> =
        localDataSource.observeAll().map { rows ->
            rows.mapNotNull { row ->
                // A row naming a kind this build has no bucket for is left out of the read rather
                // than crashed on. Local storage outlives the app version that wrote it, and going
                // forward a version and back again is the ordinary way that happens.
                val kind = SavedKind.entries.firstOrNull { it.name == row.kind }

                kind?.let { SavedItem(id = row.id, kind = it, editionId = row.edition_id) }
            }
        }

    override suspend fun toggle(item: SavedItem) {
        localDataSource.toggle(id = item.id, kind = item.kind.name, editionId = item.editionId)
    }
}
