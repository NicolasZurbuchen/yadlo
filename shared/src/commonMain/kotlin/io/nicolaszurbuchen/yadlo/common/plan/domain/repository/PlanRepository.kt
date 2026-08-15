package io.nicolaszurbuchen.yadlo.common.plan.domain.repository

import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import kotlinx.coroutines.flow.Flow

/**
 * What the visitor kept, and the only place it is written.
 *
 * It holds ids and nothing else. Nothing here knows what a Slot is, whether the Happening behind one
 * still exists, or which day it falls on — the heart is a join, not a field (SPEC.md § Domain), so
 * every screen combines this with the content bundle rather than expecting the two to have been
 * stored together. A refresh that drops a Happening therefore cannot delete anything: the saved id
 * simply stops matching, and starts matching again if the content comes back.
 */
interface PlanRepository {
    /** Everything saved, of both kinds, in no particular order — callers order by content. */
    fun observeSaved(): Flow<List<SavedItem>>

    /**
     * Saves [item] if its id is not held, removes it if it is. One operation rather than a pair,
     * because removing is the same heart tapped again and the screen should not have to be right
     * about which of the two it is asking for.
     */
    suspend fun toggle(item: SavedItem)
}
