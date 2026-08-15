package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase.ObserveWishlistUseCase
import kotlinx.coroutines.launch

interface WishlistStore : Store<WishlistIntent, WishlistState, WishlistLabel>

class WishlistStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeWishlist: ObserveWishlistUseCase,
) {
    fun create(): WishlistStore =
        object :
            WishlistStore,
            Store<WishlistIntent, WishlistState, WishlistLabel> by storeFactory.create(
                name = "WishlistStore",
                initialState = WishlistState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<WishlistAction>() {
        override fun invoke() {
            dispatch(WishlistAction.ObserveContent)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<WishlistIntent, WishlistAction, WishlistState, WishlistMessage, WishlistLabel>() {
        override fun executeAction(action: WishlistAction) {
            when (action) {
                WishlistAction.ObserveContent -> observeContent()
            }
        }

        /**
         * Observed rather than read once: a Stand can be taken off the Wishlist on its own fiche,
         * which is one screen away and returns straight back to this list.
         */
        private fun observeContent() {
            scope.launch {
                observeWishlist().collect { groups ->
                    dispatch(WishlistMessage.GroupsUpdated(groups))
                }
            }
        }
    }

    // internal (not private) so WishlistReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<WishlistState, WishlistMessage> {
        override fun WishlistState.reduce(msg: WishlistMessage): WishlistState =
            when (msg) {
                is WishlistMessage.GroupsUpdated -> {
                    copy(groups = msg.groups)
                }
            }
    }
}
