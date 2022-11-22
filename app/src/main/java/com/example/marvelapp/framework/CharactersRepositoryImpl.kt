package com.example.marvelapp.framework

import androidx.paging.PagingSource
import com.example.marvelapp.framework.network.response.DataWrapperResponse
import com.gmail.devpelegrino.core.data.repository.CharactersRemoteDataSource
import com.gmail.devpelegrino.core.data.repository.CharactersRepository
import com.gmail.devpelegrino.core.domain.model.Character
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    private val remoteDataSource: CharactersRemoteDataSource<DataWrapperResponse>
) : CharactersRepository {

    override fun getCharacters(query: String): PagingSource<Int, Character> {
        TODO("Not yet implemented")
    }
}