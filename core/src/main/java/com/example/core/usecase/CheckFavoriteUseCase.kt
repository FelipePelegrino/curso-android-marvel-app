package com.example.core.usecase

import com.example.core.data.repository.FavoritesRepository
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CheckFavoriteUseCase {

    operator fun invoke(params: Params): Flow<ResultStatus<Boolean>>

    data class Params(val characterId: Int)

}

class CheckFavoriteUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val coroutinesDispatchers: CoroutinesDispatchers
) : UseCase<CheckFavoriteUseCase.Params, Boolean>(), CheckFavoriteUseCase {

    override suspend fun doWork(params: CheckFavoriteUseCase.Params): ResultStatus<Boolean> {
        return withContext(coroutinesDispatchers.io()) {
            ResultStatus.Success(favoritesRepository.isFavorite(params.characterId))
        }
    }
}