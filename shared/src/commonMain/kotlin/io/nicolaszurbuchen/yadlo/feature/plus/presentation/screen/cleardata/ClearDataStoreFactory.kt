package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ClearImageCacheUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ClearSavedUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveSavedCountUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ReadImageCacheSizeUseCase
import kotlinx.coroutines.launch

interface ClearDataStore : Store<ClearDataIntent, ClearDataState, ClearDataLabel>

class ClearDataStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeSavedCount: ObserveSavedCountUseCase,
    private val clearSaved: ClearSavedUseCase,
    private val readImageCacheSize: ReadImageCacheSizeUseCase,
    private val clearImageCache: ClearImageCacheUseCase,
) {
    fun create(): ClearDataStore =
        object :
            ClearDataStore,
            Store<ClearDataIntent, ClearDataState, ClearDataLabel> by storeFactory.create(
                name = "ClearDataStore",
                initialState = ClearDataState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<ClearDataAction>() {
        override fun invoke() {
            dispatch(ClearDataAction.ObserveSaved)
            dispatch(ClearDataAction.ReadImageCacheSize)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ClearDataIntent, ClearDataAction, ClearDataState, ClearDataMessage, ClearDataLabel>() {
        override fun executeAction(action: ClearDataAction) {
            when (action) {
                ClearDataAction.ObserveSaved -> observeSaved()
                ClearDataAction.ReadImageCacheSize -> readCacheSize()
            }
        }

        override fun executeIntent(intent: ClearDataIntent) {
            when (intent) {
                ClearDataIntent.SavedClicked -> dispatch(ClearDataMessage.ConfirmationChanged(isAsking = true))
                ClearDataIntent.SavedDismissed -> dispatch(ClearDataMessage.ConfirmationChanged(isAsking = false))
                ClearDataIntent.SavedConfirmed -> confirmSaved()
                ClearDataIntent.ImagesClicked -> emptyImageCache()
            }
        }

        /**
         * The two numbers on this screen are read two different ways, which is the whole of what
         * this Executor does. The Plan publishes, so it is collected and the count falls to zero by
         * itself; the cache does not, so it is read again after being emptied.
         */
        private fun observeSaved() {
            scope.launch {
                observeSavedCount().collect { count ->
                    dispatch(ClearDataMessage.SavedUpdated(count))
                }
            }
        }

        private fun readCacheSize() {
            scope.launch {
                dispatch(ClearDataMessage.ImageCacheSizeUpdated(readImageCacheSize()))
            }
        }

        /**
         * The question closes first and the deletion follows, rather than the dialog waiting for the
         * write: the answer has already been given, and a modal left standing over a screen that is
         * updating underneath it reads as a tap that did not register.
         */
        private fun confirmSaved() {
            scope.launch {
                dispatch(ClearDataMessage.ConfirmationChanged(isAsking = false))
                clearSaved()
            }
        }

        private fun emptyImageCache() {
            scope.launch {
                clearImageCache()
                // Read back rather than assumed to be zero. Coil empties a directory, and what the
                // screen should show is what is there afterwards — including the case where it
                // could not.
                dispatch(ClearDataMessage.ImageCacheSizeUpdated(readImageCacheSize()))
            }
        }
    }

    // internal (not private) so ClearDataReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<ClearDataState, ClearDataMessage> {
        override fun ClearDataState.reduce(msg: ClearDataMessage): ClearDataState =
            when (msg) {
                is ClearDataMessage.SavedUpdated -> {
                    copy(savedCount = msg.count)
                }

                is ClearDataMessage.ImageCacheSizeUpdated -> {
                    copy(imageCacheBytes = msg.bytes)
                }

                is ClearDataMessage.ConfirmationChanged -> {
                    copy(isAskingAboutSaved = msg.isAsking)
                }
            }
    }
}
