package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    factory: HomeStoreFactory,
) : ViewModel() {
    private val store = factory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeUiModel> =
        store.stateFlow
            .map { it.toUiModel() }
            // The store's own state, not a reconstructed one: HomeState carries an instant, and
            // the only thing entitled to read a clock is the factory that already did.
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), store.state.toUiModel())

    val labels: Flow<HomeLabel> = store.labels

    fun onIntent(intent: HomeIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
