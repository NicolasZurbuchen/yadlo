package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClearDataViewModel(
    factory: ClearDataStoreFactory,
) : ViewModel() {
    private val store = factory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ClearDataUiModel> =
        store.stateFlow
            .map { it.toUiModel() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), store.state.toUiModel())

    // No `labels`: ClearDataLabel has no members, because nothing on this screen leads anywhere.

    fun onIntent(intent: ClearDataIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
