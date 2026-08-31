package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

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

class StandsViewModel(
    factory: StandsStoreFactory,
    kind: StandsKindUiModel,
) : ViewModel() {
    private val store = factory.create(kind)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<StandsUiModel> =
        store.stateFlow
            .map { it.toUiModel() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), store.state.toUiModel())

    val labels: Flow<StandsLabel> = store.labels

    fun onIntent(intent: StandsIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
