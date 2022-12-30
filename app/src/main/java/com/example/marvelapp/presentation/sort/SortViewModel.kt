package com.example.marvelapp.presentation.sort

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.GetCharactersSortingUseCase
import com.example.core.usecase.SaveCharactersSortingUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.marvelapp.presentation.extension.watchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor(
    private val getCharactersSortingUseCase: GetCharactersSortingUseCase,
    private val saveCharactersSortingUseCase: SaveCharactersSortingUseCase,
    private val coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {

    private val action = MutableLiveData<Action>()
    val state: LiveData<UiState> = action.switchMap { action ->
        liveData(coroutinesDispatchers.main()) {
            when (action) {
                Action.GetStoredSorting -> {
                    getCharactersSortingUseCase.invoke().collect { sortingPair ->
                        emit(UiState.SortingResult(sortingPair))
                    }
                }
                is Action.ApplySorting -> {
                    saveCharactersSortingUseCase.invoke(
                        SaveCharactersSortingUseCase.Params(
                            action.orderBy to action.order
                        )
                    ).watchStatus(
                        success = {
                            emit(UiState.ApplyState.Success)
                        },
                        loading = {
                            emit(UiState.ApplyState.Loading)
                        },
                        error = {
                            emit(UiState.ApplyState.Error)
                        }
                    )
                }
            }
        }
    }

    init {
        action.value = Action.GetStoredSorting
    }

    fun applySorting(orderBy: String, order: String) {
        action.value = Action.ApplySorting(
            orderBy,
            order
        )
    }

    sealed class Action {
        object GetStoredSorting : Action()
        data class ApplySorting(
            val orderBy: String,
            val order: String
        ) : Action()
    }

    sealed class UiState {
        data class SortingResult(val storedSorting: Pair<String, String>) : UiState()

        sealed class ApplyState : UiState() {
            object Loading : ApplyState()
            object Success : ApplyState()
            object Error : ApplyState()
        }
    }
}