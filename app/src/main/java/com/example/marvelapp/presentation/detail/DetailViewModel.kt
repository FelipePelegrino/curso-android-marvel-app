package com.example.marvelapp.presentation.detail

import androidx.lifecycle.ViewModel
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.RemoveFavoriteUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    addFavoriteUseCase: AddFavoriteUseCase,
    checkFavoriteUseCase: CheckFavoriteUseCase,
    getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase,
    removeFavoriteUseCase: RemoveFavoriteUseCase,
    coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {

    val categories = UiActionStateLiveData(
        getCharacterCategoriesUseCase = getCharacterCategoriesUseCase,
        coroutineContext = coroutinesDispatchers.main()
    )

    val favorite = FavoriteUiActionStateLiveData(
        addFavoriteUseCase = addFavoriteUseCase,
        checkFavoriteUseCase = checkFavoriteUseCase,
        removeFavoriteUseCase = removeFavoriteUseCase,
        coroutineContext = coroutinesDispatchers.main()
    )
}