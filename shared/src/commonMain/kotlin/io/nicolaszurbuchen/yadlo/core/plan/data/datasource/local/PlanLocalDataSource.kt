package io.nicolaszurbuchen.yadlo.core.plan.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface PlanLocalDataSource {
    fun observeAll(): Flow<List<SavedEntry>>

    /**
     * Inserts the row if its id is absent and deletes it if present, in one transaction. Doing it
     * here rather than as a read followed by a write in the repository is what keeps two taps
     * arriving together from both reading "not saved" and both inserting.
     */
    suspend fun toggle(
        id: String,
        kind: String,
        editionId: String,
    )

    /** Every row, of both kinds and every edition. */
    suspend fun deleteAll()
}
