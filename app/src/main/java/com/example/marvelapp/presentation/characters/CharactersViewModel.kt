package com.example.marvelapp.presentation.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.core.domain.model.Character
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {

    var currentSearchQuery = ""

    private val action = MutableLiveData<Action>()

    val state: LiveData<UiState> = action
        .switchMap { action ->
            when (action) {
                is Action.Search, Action.Sort -> {
                    getCharactersPagingData(currentSearchQuery)
                }
            }
        }

    fun searchCharacters() {
        action.value = Action.Search
    }

    fun applySort() {
        action.value = Action.Sort
    }

    fun closeSearch() {
        if (currentSearchQuery.isNotEmpty()) {
            currentSearchQuery = ""
        }
    }

    private fun getCharactersPagingData(
        query: String
    ): LiveData<UiState> =
        getCharactersUseCase(
            GetCharactersUseCase.GetCharactersParams(query, getPageConfig())
        ).cachedIn(viewModelScope).map {
            UiState.SearchResult(it)
        }.asLiveData(coroutinesDispatchers.main())

    private fun getPageConfig() = PagingConfig(
        pageSize = 20
    )

    private sealed class Action {
        object Search : Action()
        object Sort : Action()
    }

    sealed class UiState {
        data class SearchResult(val data: PagingData<Character>) : UiState()
    }
}