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
import com.example.core.usecase.RemoveFavoriteUseCase
import com.example.marvelapp.R
import com.example.marvelapp.presentation.extension.watchStatus
import kotlin.coroutines.CoroutineContext

class FavoriteUiActionStateLiveData(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val coroutineContext: CoroutineContext
) {

    private var currentFavoriteIcon = R.drawable.ic_favorite_unchecked

    private val action = MutableLiveData<Action>()

    val state: LiveData<UiState> = action.switchMap { action ->
        liveData(coroutineContext) {
            when (action) {
                is Action.AddFavorite -> {
                    handleActionAddFavorite(action)
                }
                is Action.CheckFavorite -> {
                    handleActionCheckFavorite(action)
                }
                is Action.RemoveFavorite -> {
                    handleRemoveFavorite(action)
                }
            }
        }
    }

    fun checkFavorite(characterId: Int) {
        action.value = Action.CheckFavorite(characterId)
    }

    fun update(detailViewArg: DetailViewArg) {
        action.value = if (currentFavoriteIcon == R.drawable.ic_favorite_unchecked) {
            Action.AddFavorite(detailViewArg)
        } else Action.RemoveFavorite(detailViewArg)
    }

    private suspend fun LiveDataScope<UiState>.handleActionAddFavorite(
        action: Action.AddFavorite
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
                success = {
                    currentFavoriteIcon = R.drawable.ic_favorite_checked
                    emitFavoriteIcon()
                }
            )
        }
    }

    private suspend fun LiveDataScope<UiState>.handleRemoveFavorite(
        action: Action.RemoveFavorite
    ) {
        action.detailViewArg.run {
            removeFavoriteUseCase(
                RemoveFavoriteUseCase.Params(
                    characterId = characterId,
                    name = name,
                    imageUrl = imageUrl
                )
            ).watchStatus(
                loading = { emit(UiState.Loading) },
                error = { emit(UiState.Error(R.string.error_remove_favorite)) },
                success = {
                    currentFavoriteIcon = R.drawable.ic_favorite_unchecked
                    emitFavoriteIcon()
                }
            )
        }
    }

    private suspend fun LiveDataScope<UiState>.handleActionCheckFavorite(
        action: Action.CheckFavorite
    ) {
        checkFavoriteUseCase(CheckFavoriteUseCase.Params(action.characterId)).watchStatus(
            error = { },
            success = { isFavorite ->
                if (isFavorite) {
                    currentFavoriteIcon = R.drawable.ic_favorite_checked
                }
                emitFavoriteIcon()
            }
        )
    }

    private suspend fun LiveDataScope<UiState>.emitFavoriteIcon() {
        emit(UiState.Icon(currentFavoriteIcon))
    }

    private sealed class Action {
        data class CheckFavorite(val characterId: Int) : Action()
        data class AddFavorite(val detailViewArg: DetailViewArg) : Action()
        data class RemoveFavorite(val detailViewArg: DetailViewArg) : Action()
    }

    sealed class UiState {
        object Loading : UiState()
        data class Icon(@DrawableRes val icon: Int) : UiState()
        data class Error(@StringRes val messageId: Int) : UiState()
    }
}