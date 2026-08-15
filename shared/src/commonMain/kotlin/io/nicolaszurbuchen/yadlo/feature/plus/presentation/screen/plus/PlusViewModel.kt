package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

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

class PlusViewModel(
    factory: PlusStoreFactory,
) : ViewModel() {
    private val store = factory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<PlusUiModel> =
        store.stateFlow
            .map { it.toUiModel() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), store.state.toUiModel())

    val labels: Flow<PlusLabel> = store.labels

    fun onIntent(intent: PlusIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
