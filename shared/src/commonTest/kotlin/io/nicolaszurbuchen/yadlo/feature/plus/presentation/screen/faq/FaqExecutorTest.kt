package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveFaqUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.festival
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ready
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FaqExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCreate_beforeAnyBundle_hasNoEntries() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.entries)
            store.dispose()
        }

    @Test
    fun onCreate_aBundleWithNoQuestions_isReadAndEmpty() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.entries?.isEmpty() == true)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_holdsTheQuestions() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = festival { copy(faq = listOf(entry("entree"))) }))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("entree"), store.state.entries?.map { it.id })
            store.dispose()
        }

    @Test
    fun refresh_landsAnAnswerMidFestival_andTheScreenFollowsIt() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(ready(festival = festival { copy(faq = listOf(entry("entree"))) }))
            testDispatcher.scheduler.runCurrent()

            repository.emitStatus(
                ready(festival = festival { copy(faq = listOf(entry("entree"), entry("chiens"))) }),
            )
            testDispatcher.scheduler.runCurrent()

            // The FAQ is the screen a correction is most likely to be pushed to, which is why it
            // observes rather than reading once at open.
            assertEquals(listOf("entree", "chiens"), store.state.entries?.map { it.id })
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): FaqStore =
        FaqStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeFaq = ObserveFaqUseCase(repository),
        ).create()

    private fun entry(id: String) = FaqEntry(id = id, question = "…", answer = "…", provenance = Provenance.CONFIRMED)
}
