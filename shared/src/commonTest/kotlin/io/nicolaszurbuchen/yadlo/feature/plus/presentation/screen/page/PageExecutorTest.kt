package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Charter
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusPageUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class PageExecutorTest {
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
    fun onCreate_theKindFromTheDestination_reachesTheState() =
        runTest {
            val store = createStore(FakeContentRepository(), PageKind.SOCIAL)
            testDispatcher.scheduler.runCurrent()

            // Translated once at construction so nothing downstream has to name a domain type.
            assertEquals(PageKind.SOCIAL, store.state.kind)
            store.dispose()
        }

    @Test
    fun onCreate_readsTheSectionTheDestinationAskedFor() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository, PageKind.RESPONSIBLE)

            repository.emitStatus(ready(festival = published()))
            testDispatcher.scheduler.runCurrent()

            // The same store, the same content, and a different page id gives a different page —
            // which is the whole reason there is one screen instead of four.
            assertEquals(listOf("festiplus"), store.state.page?.sections?.map { it.id })
            store.dispose()
        }

    @Test
    fun onCreate_theOtherPageId_readsTheOtherSection() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository, PageKind.SOCIAL)

            repository.emitStatus(ready(festival = published()))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("Instagram"), store.state.page?.sections?.single()?.links?.map { it.label })
            store.dispose()
        }

    @Test
    fun linkClicked_publishesTheUrlForThePlatformToOpen() =
        runTest {
            val store = createStore(FakeContentRepository(), PageKind.SOCIAL)
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(PageIntent.LinkClicked("https://festiplus.ch/"))
                assertEquals(PageLabel.OpenUrl("https://festiplus.ch/"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(
        repository: FakeContentRepository,
        kind: PageKind,
    ): PageStore =
        PageStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observePlusPage = ObservePlusPageUseCase(repository),
            kind = kind,
        ).create()

    private fun published() =
        festival {
            copy(
                charters =
                    listOf(
                        Charter(
                            id = "festiplus",
                            name = "FestiPlus",
                            body = "Une charte.",
                            url = "https://festiplus.ch/",
                            provenance = Provenance.CONFIRMED,
                        ),
                    ),
                social = listOf(SocialLink(id = "instagram", name = "Instagram", url = "https://example.ch/")),
            )
        }
}
