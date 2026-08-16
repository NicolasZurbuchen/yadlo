package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Story
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStoryPageUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.festival
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.figure
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

@OptIn(ExperimentalCoroutinesApi::class)
class StoryExecutorTest {
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
    fun onCreate_beforeAnyBundle_carriesNoPage() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.page)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_joinsTheStoryToTheEditionsFigures() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(
                ready(
                    festival =
                        festival {
                            copy(
                                story =
                                    Story(
                                        foundedYear = 2015,
                                        body = "Yadlo est né en 2015.",
                                        passage = null,
                                        provenance = Provenance.CONFIRMED,
                                    ),
                            )
                        },
                    figures = listOf(figure("visiteurs", "6000")),
                ),
            )
            testDispatcher.scheduler.runCurrent()

            // Two files, one screen: the origin from the live-truth file, the numbers from the
            // Edition that they belong to.
            assertEquals(2015, store.state.page?.foundedYear)
            assertEquals(listOf("6000"), store.state.page?.figures?.map { it.value })
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): StoryStore =
        StoryStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeStoryPage = ObserveStoryPageUseCase(repository),
        ).create()
}
