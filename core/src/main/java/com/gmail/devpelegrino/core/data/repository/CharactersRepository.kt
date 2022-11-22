package com.gmail.devpelegrino.core.data.repository

import androidx.paging.PagingSource
import com.gmail.devpelegrino.core.domain.model.Character

interface CharactersRepository {

    fun getCharacters(query: String): PagingSource<Int, Character>
}