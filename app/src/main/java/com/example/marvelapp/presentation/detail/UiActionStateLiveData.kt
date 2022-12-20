package com.example.marvelapp.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.core.domain.model.Comic
import com.example.core.domain.model.Event
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.marvelapp.R
import com.example.marvelapp.presentation.extension.watchStatus
import kotlin.coroutines.CoroutineContext

class UiActionStateLiveData(
    private val coroutineContext: CoroutineContext,
    private val getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase
) {

    private val action = MutableLiveData<Action>()
    val state: LiveData<UiState> = action.switchMap { action ->
        liveData(coroutineContext) {
            when (action) {
                is Action.Load -> {
                    getCharacterCategoriesUseCase(
                        GetCharacterCategoriesUseCase.GetCharacterCategoriesParams(action.characterId)
                    ).watchStatus(
                        loading = { emit(UiState.Loading) },
                        error = { emit(UiState.Error) },
                        success = { resultData ->
                            val detailParentList = mutableListOf<DetailParentVE>()

                            getCharacterCategories(resultData, detailParentList)
                            getEvents(resultData, detailParentList)

                            if (detailParentList.isNotEmpty()) {
                                emit(UiState.Success(detailParentList))
                            } else emit(UiState.Empty)
                        },
                    )
                }
            }
        }
    }

    fun load(characterId: Int) {
        action.value = Action.Load(characterId)
    }

    private fun getCharacterCategories(
        resultData: Pair<List<Comic>, List<Event>>,
        detailParentList: MutableList<DetailParentVE>
    ) {
        val comics = resultData.first
        if (comics.isNotEmpty()) {
            comics.map {
                DetailChildVE(it.id, it.imageUrl)
            }.also {
                detailParentList.add(
                    DetailParentVE(R.string.details_comics_category, it)
                )
            }
        }
    }

    private fun getEvents(
        resultData: Pair<List<Comic>, List<Event>>,
        detailParentList: MutableList<DetailParentVE>
    ) {
        val events = resultData.second
        if (events.isNotEmpty()) {
            events.map {
                DetailChildVE(it.id, it.imageUrl)
            }.also {
                detailParentList.add(
                    DetailParentVE(R.string.details_events_category, it)
                )
            }
        }
    }

    private sealed class Action {
        data class Load(val characterId: Int) : Action()
    }

    sealed class UiState {
        data class Success(val detailParentList: List<DetailParentVE>) : UiState()
        object Loading : UiState()
        object Error : UiState()
        object Empty : UiState()
    }
}