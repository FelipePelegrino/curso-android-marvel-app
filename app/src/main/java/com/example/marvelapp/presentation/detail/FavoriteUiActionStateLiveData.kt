package com.example.marvelapp.presentation.detail

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.AddFavoriteUseCase
import com.example.marvelapp.R
import com.example.marvelapp.presentation.extension.watchStatus
import kotlin.coroutines.CoroutineContext

class FavoriteUiActionStateLiveData(
    private val coroutineContext: CoroutineContext,
    private val addFavoriteUseCase: AddFavoriteUseCase
) {

    private val action = MutableLiveData<Action>()

    val state: LiveData<UiState> = action.switchMap { action ->
        liveData(coroutineContext) {
            when (action) {
                Action.Default -> {
                    emit(UiState.Icon(R.drawable.ic_favorite_unchecked))
                }
                is Action.Update -> {
                    action.detailViewArg.run {
                        addFavoriteUseCase.invoke(
                            AddFavoriteUseCase.Params(
                                characterId = characterId,
                                name = name,
                                imageUrl = imageUrl
                            )
                        ).watchStatus(
                            loading = { emit(UiState.Loading) },
                            error = { emit(UiState.Error(R.string.error_add_favorite)) },
                            success = { emit(UiState.Icon(R.drawable.ic_favorite_checked)) }
                        )
                    }
                }
            }
        }
    }

    fun setDefault() {
        action.value = Action.Default
    }

    fun update(detailViewArg: DetailViewArg) {
        action.value = Action.Update(detailViewArg)
    }

    private sealed class Action {
        object Default : Action()
        data class Update(val detailViewArg: DetailViewArg) : Action()
    }

    sealed class UiState {
        object Loading : UiState()
        data class Icon(@DrawableRes val icon: Int) : UiState()
        data class Error(@StringRes val messageId: Int) : UiState()
    }
}