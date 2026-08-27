package io.nicolaszurbuchen.yadlo.core.plan.data.datasource.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class PlanLocalDataSourceImpl(
    private val queries: SavedEntryQueries,
) : PlanLocalDataSource {
    override fun observeAll(): Flow<List<SavedEntry>> = queries.selectAll().asFlow().mapToList(Dispatchers.Default)

    override suspend fun toggle(
        id: String,
        kind: String,
        editionId: String,
    ) {
        queries.transaction {
            if (queries.selectById(id).executeAsOneOrNull() == null) {
                queries.upsert(id = id, kind = kind, edition_id = editionId)
            } else {
                queries.deleteById(id)
            }
        }
    }

    override suspend fun deleteAll() {
        queries.deleteAll()
    }
}
