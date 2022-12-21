package com.example.marvelapp.presentation.detail

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.marvelapp.R
import com.example.marvelapp.presentation.extension.watchStatus
import kotlin.coroutines.CoroutineContext

class FavoriteUiActionStateLiveData(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    private val coroutineContext: CoroutineContext
) {

    private val action = MutableLiveData<Action>()

    val state: LiveData<UiState> = action.switchMap { action ->
        liveData(coroutineContext) {
            when (action) {
                is Action.CheckFavorite -> {
                    handleActionCheckFavorite(action)
                }
                is Action.Update -> {
                    handleActionUpdate(action)
                }
            }
        }
    }

    fun checkFavorite(characterId: Int) {
        action.value = Action.CheckFavorite(characterId)
    }

    fun update(detailViewArg: DetailViewArg) {
        action.value = Action.Update(detailViewArg)
    }

    private suspend fun LiveDataScope<UiState>.handleActionUpdate(
        action: Action.Update
    ) {
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

    private suspend fun LiveDataScope<UiState>.handleActionCheckFavorite(
        action: Action.CheckFavorite
    ) {
        checkFavoriteUseCase(CheckFavoriteUseCase.Params(action.characterId)).watchStatus(
            error = { },
            success = { isFavorite ->
                val favoriteIcon =
                    if (isFavorite) {
                        R.drawable.ic_favorite_checked
                    } else R.drawable.ic_favorite_unchecked
                emit(UiState.Icon(favoriteIcon))
            }
        )
    }

    private sealed class Action {
        data class CheckFavorite(val characterId: Int) : Action()
        data class Update(val detailViewArg: DetailViewArg) : Action()
    }

    sealed class UiState {
        object Loading : UiState()
        data class Icon(@DrawableRes val icon: Int) : UiState()
        data class Error(@StringRes val messageId: Int) : UiState()
    }
}