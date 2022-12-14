package com.example.core.usecase

import com.example.core.data.repository.CharactersRepository
import com.example.core.domain.model.Comic
import com.example.core.domain.model.Event
import com.example.core.usecase.GetCharacterCategoriesUseCase.GetComicsParams
import com.example.core.usecase.base.AppCoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GetCharacterCategoriesUseCase {

    operator fun invoke(params: GetComicsParams): Flow<ResultStatus<Pair<List<Comic>, List<Event>>>>

    data class GetComicsParams(val characterId: Int)
}

class GetCharacterCategoriesUseCaseImpl @Inject constructor(
    private val charactersRepository: CharactersRepository,
    private val dispatchers: AppCoroutinesDispatchers
) : GetCharacterCategoriesUseCase, UseCase<GetComicsParams, Pair<List<Comic>, List<Event>>>() {

    override suspend fun doWork(
        params: GetComicsParams
    ): ResultStatus<Pair<List<Comic>, List<Event>>> {
        return withContext(dispatchers.io) {
            val comicsDeferred = async { charactersRepository.getComics(params.characterId) }
            val eventsDeferred = async { charactersRepository.getEvents(params.characterId) }

            val comics = comicsDeferred.await()
            val events = eventsDeferred.await()

            // comics to events = Pair(comics, events)
            ResultStatus.Success(comics to events)
        }
    }
}