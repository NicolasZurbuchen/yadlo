package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

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

class HappeningViewModel(
    factory: HappeningStoreFactory,
    happeningId: String,
) : ViewModel() {
    private val store = factory.create(happeningId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HappeningUiModel> =
        store.stateFlow
            .map { it.toUiModel() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), store.state.toUiModel())

    val labels: Flow<HappeningLabel> = store.labels

    fun onIntent(intent: HappeningIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
