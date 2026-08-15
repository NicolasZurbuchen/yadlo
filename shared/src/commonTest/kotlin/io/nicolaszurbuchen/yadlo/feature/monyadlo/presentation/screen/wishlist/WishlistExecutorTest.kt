package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase.ObserveWishlistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
class WishlistExecutorTest {
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
    fun onCreate_beforeAnyBundle_hasNoListRatherThanAnEmptyOne() =
        wishlistTest { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.groups)
        }

    @Test
    fun onCreate_theBundleArrivesWithNothingKept_isReadAndEmpty() =
        wishlistTest { store, content, _ ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.groups?.isEmpty() == true)
        }

    @Test
    fun onCreate_theBundleArrivesWithSomethingKept_groupsItByCategory() =
        wishlistTest(saved = listOf(savedStand("vegan-fabrik"))) { store, content, _ ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("restauration"), store.state.groups?.map { it.categoryId })
        }

    @Test
    fun standRemovedOnItsFiche_dropsOutOfTheListItWasOpenedFrom() =
        wishlistTest(saved = listOf(savedStand("vegan-fabrik"))) { store, content, plan ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()
            assertEquals(1, store.state.groups?.size)

            // One tap away and straight back: the fiche is reached from this very row.
            plan.toggle(savedStand("vegan-fabrik"))
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.groups?.isEmpty() == true)
        }

    private fun wishlistTest(
        saved: List<SavedItem> = emptyList(),
        block: suspend TestScope.(WishlistStore, FakeContentRepository, FakePlanRepository) -> Unit,
    ) = runTest {
        val contentRepository = FakeContentRepository()
        val planRepository = FakePlanRepository().apply { emitSaved(saved) }
        val store =
            WishlistStoreFactory(
                storeFactory = DefaultStoreFactory(),
                observeWishlist = ObserveWishlistUseCase(contentRepository, planRepository),
            ).create()

        try {
            block(store, contentRepository, planRepository)
        } finally {
            store.dispose()
        }
    }

    private fun savedStand(id: String) = SavedItem(id = id, kind = SavedKind.STAND, editionId = "2026")

    private fun ready() =
        ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival =
                        Festival(
                            name = "Yadlo",
                            tagline = "Mouille ton corps, arrose ton esprit",
                            currentEditionId = "2026",
                            minSupportedAppVersion = null,
                            social = emptyList(),
                        ),
                    edition =
                        Edition(
                            id = "2026",
                            year = 2026,
                            name = "Yadlo 2026",
                            venue =
                                Venue(
                                    name = "Plage de Préverenges",
                                    address = "Route de la Plage, 1028 Préverenges",
                                    latitude = 46.51,
                                    longitude = 6.53,
                                    provenance = Provenance.CONFIRMED,
                                ),
                            days = emptyList(),
                            categories = listOf(FOOD),
                            happenings =
                                listOf(
                                    Happening.Stand(
                                        id = "vegan-fabrik",
                                        name = "Vegan Fabrik",
                                        category = FOOD,
                                        description = null,
                                        images = emptyList(),
                                        provenance = Provenance.CONFIRMED,
                                        offering = "Cuisine végétale",
                                        marks = emptyList(),
                                        links = emptyList(),
                                        menu = emptyList(),
                                    ),
                                ),
                            slots = emptyList(),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
        )

    private companion object {
        val FOOD = Category(id = "restauration", name = "Restauration", order = 6)
    }
}
