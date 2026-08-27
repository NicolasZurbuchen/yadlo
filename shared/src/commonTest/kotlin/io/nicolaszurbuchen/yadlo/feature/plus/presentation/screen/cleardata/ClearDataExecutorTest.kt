package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.core.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.feature.plus.domain.fake.FakeImageCache
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.SavedCount
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ClearImageCacheUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ClearSavedUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveSavedCountUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ReadImageCacheSizeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ClearDataExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    private val plan = FakePlanRepository()
    private val images = FakeImageCache()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCreate_readsBothNumbers() =
        runTest {
            plan.emitSaved(listOf(slot("2026:dj-alf-fri"), stand("vegan-fabrik")))
            images.bytes = 4_823_000L

            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            assertEquals(SavedCount(slots = 1, stands = 1), store.state.savedCount)
            assertEquals(4_823_000L, store.state.imageCacheBytes)
            store.dispose()
        }

    @Test
    fun onCreate_withNothingKept_readsZerosRatherThanLeavingThemUnknown() =
        runTest {
            // Null is the skeleton and zero is the sentence. A fresh install has to reach the
            // second, or the screen never finishes loading.
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            assertEquals(SavedCount(slots = 0, stands = 0), store.state.savedCount)
            assertEquals(0L, store.state.imageCacheBytes)
            store.dispose()
        }

    @Test
    fun savedClicked_asksAndRemovesNothing() =
        runTest {
            plan.emitSaved(listOf(slot("2026:dj-alf-fri")))
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.SavedClicked)
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.isAskingAboutSaved)
            assertEquals(0, plan.cleared)
            assertEquals(SavedCount(slots = 1, stands = 0), store.state.savedCount)
            store.dispose()
        }

    @Test
    fun savedDismissed_closesTheQuestionAndRemovesNothing() =
        runTest {
            plan.emitSaved(listOf(slot("2026:dj-alf-fri")))
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.SavedClicked)
            store.accept(ClearDataIntent.SavedDismissed)
            testDispatcher.scheduler.runCurrent()

            assertFalse(store.state.isAskingAboutSaved)
            assertEquals(0, plan.cleared)
            store.dispose()
        }

    @Test
    fun savedConfirmed_clearsThePlanAndClosesTheQuestion() =
        runTest {
            plan.emitSaved(listOf(slot("2026:dj-alf-fri"), stand("vegan-fabrik")))
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.SavedClicked)
            store.accept(ClearDataIntent.SavedConfirmed)
            testDispatcher.scheduler.runCurrent()

            assertFalse(store.state.isAskingAboutSaved)
            assertEquals(1, plan.cleared)
            store.dispose()
        }

    @Test
    fun savedConfirmed_lettingTheCountFallOnItsOwn() =
        runTest {
            // Nothing dispatches the new count: the Plan publishes, and the collector started at
            // bootstrap is what puts zero on the screen. That is the whole reason this half is
            // observed and the cache is not.
            plan.emitSaved(listOf(slot("2026:dj-alf-fri")))
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.SavedConfirmed)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SavedCount(slots = 0, stands = 0), store.state.savedCount)
            store.dispose()
        }

    @Test
    fun savedConfirmed_leavesTheImagesAlone() =
        runTest {
            // Two buttons, two effects. A confirmation on one that emptied both would be the exact
            // surprise the dialog exists to prevent.
            images.bytes = 4_823_000L
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.SavedConfirmed)
            testDispatcher.scheduler.runCurrent()

            assertEquals(0, images.cleared)
            assertEquals(4_823_000L, store.state.imageCacheBytes)
            store.dispose()
        }

    @Test
    fun imagesClicked_emptiesTheCacheWithoutAsking() =
        runTest {
            images.bytes = 4_823_000L
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.ImagesClicked)
            testDispatcher.scheduler.runCurrent()

            assertEquals(1, images.cleared)
            assertFalse(store.state.isAskingAboutSaved)
            store.dispose()
        }

    @Test
    fun imagesClicked_readsTheSizeBackRatherThanAssumingZero() =
        runTest {
            // A cache Coil could not fully delete has to be reported as what is left. Assuming zero
            // would leave a screen saying it is empty next to a button that is still enabled.
            images.bytes = 4_823_000L
            images.failsToClear = true
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.ImagesClicked)
            testDispatcher.scheduler.runCurrent()

            assertEquals(4_823_000L, store.state.imageCacheBytes)
            store.dispose()
        }

    @Test
    fun imagesClicked_leavesThePlanAlone() =
        runTest {
            plan.emitSaved(listOf(slot("2026:dj-alf-fri")))
            val store = createStore()
            testDispatcher.scheduler.runCurrent()

            store.accept(ClearDataIntent.ImagesClicked)
            testDispatcher.scheduler.runCurrent()

            assertEquals(0, plan.cleared)
            assertEquals(SavedCount(slots = 1, stands = 0), store.state.savedCount)
            store.dispose()
        }

    private fun createStore(): ClearDataStore =
        ClearDataStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeSavedCount = ObserveSavedCountUseCase(plan),
            clearSaved = ClearSavedUseCase(plan),
            readImageCacheSize = ReadImageCacheSizeUseCase(images),
            clearImageCache = ClearImageCacheUseCase(images),
        ).create()

    private fun slot(id: String) = SavedItem(id = id, kind = SavedKind.SLOT, editionId = "2026")

    private fun stand(id: String) = SavedItem(id = id, kind = SavedKind.STAND, editionId = "2026")
}
