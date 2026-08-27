package io.nicolaszurbuchen.yadlo.core.plan.data.repository

import io.nicolaszurbuchen.yadlo.core.plan.data.datasource.local.PlanLocalDataSource
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.core.plan.domain.repository.PlanRepository
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

    /**
     * Rows this build cannot read go too. [observeSaved] drops a row whose kind it has no bucket
     * for, so filtering here would leave behind exactly the rows the visitor was never shown — and
     * a delete that quietly keeps the part you could not see is the wrong answer to *effacer mes
     * données*.
     */
    override suspend fun clear() {
        localDataSource.deleteAll()
    }
}
