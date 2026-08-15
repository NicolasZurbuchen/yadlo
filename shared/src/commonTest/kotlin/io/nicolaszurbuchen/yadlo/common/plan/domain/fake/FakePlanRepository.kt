package io.nicolaszurbuchen.yadlo.common.plan.domain.fake

import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared for the same reason [io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository]
 * is: the Plan is in `common/`, and every screen that draws a heart or recalls one joins against it.
 *
 * [toggle] really toggles rather than recording the call and standing still, so a test can tap the
 * same heart twice and read what a second tap does.
 */
class FakePlanRepository : PlanRepository {
    private val saved = MutableStateFlow<List<SavedItem>>(emptyList())

    val toggled: MutableList<SavedItem> = mutableListOf()

    override fun observeSaved(): Flow<List<SavedItem>> = saved.asStateFlow()

    override suspend fun toggle(item: SavedItem) {
        toggled += item
        saved.value =
            if (saved.value.any { it.id == item.id }) {
                saved.value.filterNot { it.id == item.id }
            } else {
                saved.value + item
            }
    }

    fun emitSaved(items: List<SavedItem>) {
        saved.value = items
    }
}
