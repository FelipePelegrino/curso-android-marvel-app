package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.factory.response.CharacterPagingFactory
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharacterFactory
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CharactersPagingSourceTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var remoteDataSource: CharactersRemoteDataSource

    private lateinit var charactersPagingSource: CharactersPagingSource

    private val characterPagingFactory = CharacterPagingFactory()

    private val characterFactory = CharacterFactory()

    @Before
    fun setUp() {
        charactersPagingSource = CharactersPagingSource(
            remoteDataSource = remoteDataSource,
            query = ""
        )
    }


    @Test
    fun `should return a success load result when load is called`() = runBlockingTest {
        // Arrange
        whenever(
            remoteDataSource.fetchCharacters(
                queries = hashMapOf(
                    "offset" to 0.toString()
                )
            )
        ).thenReturn(
            characterPagingFactory.create()
        )

        // Act
        val result = charactersPagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        // Assert
        val expected = PagingSource.LoadResult.Page(
            data = listOf(
                characterFactory.create(CharacterFactory.Hero.ThreeD),
                characterFactory.create(CharacterFactory.Hero.ABomb)
            ),
            prevKey = null,
            nextKey = 20
        )

        assertEquals(
            expected,
            result
        )
    }

    @Test
    fun `should return a error load result when load is called`() = runBlockingTest {
        // Arrange
        val exception = RuntimeException()

        whenever(
            remoteDataSource.fetchCharacters(
                queries = hashMapOf(
                    "offset" to 0.toString()
                )
            )
        ).thenThrow(exception)

        // Act
        val result = charactersPagingSource.load(
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        // Assert
        val expected = PagingSource.LoadResult.Error<Int, Character>(exception)
        assertEquals(expected, result)
    }

}