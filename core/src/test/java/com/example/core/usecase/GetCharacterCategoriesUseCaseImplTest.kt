package com.example.core.usecase

import com.example.core.data.repository.CharactersRepository
import com.example.core.usecase.base.ResultStatus
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharacterFactory
import com.example.testing.model.ComicFactory
import com.example.testing.model.EventFactory
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetCharacterCategoriesUseCaseImplTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var characterRepository: CharactersRepository

    private lateinit var getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase

    private val character = CharacterFactory().create(CharacterFactory.Hero.ThreeD)
    private val comics = listOf(ComicFactory().create(ComicFactory.FakeComic.FakeComic1))
    private val events = listOf(EventFactory().create(EventFactory.FakeEvent.FakeEvent1))

    @Before
    fun setUp() {
        getCharacterCategoriesUseCase = GetCharacterCategoriesUseCaseImpl(
            charactersRepository = characterRepository,
            dispatchers = mainCoroutineRule.testDispatcherProvider
        )
    }

    @Test
    fun `should return Success from ResultStatus when get both requests return success`() =
        runTest {
            // Arrange
            whenever(characterRepository.getComics(character.id))
                .thenReturn(comics)
            whenever(characterRepository.getEvents(character.id))
                .thenReturn(events)

            // Act
            val result = getCharacterCategoriesUseCase(
                GetCharacterCategoriesUseCase.GetCharacterCategoriesParams(character.id)
            ).toList()
            // Assert
            val expected = flow {
                emit(ResultStatus.Loading)
                emit(
                    ResultStatus.Success(
                        comics to events
                    )
                )
            }.toList()

            assertEquals(ResultStatus.Loading, result[0])
            assertTrue(result[1] is ResultStatus.Success)
            assertEquals(expected, result)
        }

    @Test
    fun `should return Error from ResultStatus when get events request returns error`() =
        runTest {
            // Arrange
            whenever(characterRepository.getComics(character.id))
                .thenReturn(comics)
            whenever(characterRepository.getEvents(character.id))
                .thenAnswer { throw Throwable() }

            // Act
            val result = getCharacterCategoriesUseCase(
                GetCharacterCategoriesUseCase.GetCharacterCategoriesParams(character.id)
            ).toList()

            // Assert

            assertTrue(result[0] is ResultStatus.Loading)
            assertTrue(result[1] is ResultStatus.Error)
        }

    @Test
    fun `should return Error from ResultStatus when get comics request returns error`() =
        runTest {
            // Arrange
            whenever(characterRepository.getComics(character.id))
                .thenAnswer { throw Throwable() }

            // Act
            val result = getCharacterCategoriesUseCase(
                GetCharacterCategoriesUseCase.GetCharacterCategoriesParams(character.id)
            ).toList()

            // Assert

            assertTrue(result[0] is ResultStatus.Loading)
            assertTrue(result[1] is ResultStatus.Error)
        }
}