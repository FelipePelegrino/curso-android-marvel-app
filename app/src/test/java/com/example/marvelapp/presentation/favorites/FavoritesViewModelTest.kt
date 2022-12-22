package com.example.marvelapp.presentation.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.core.usecase.GetFavoritesUseCase
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharacterFactory
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FavoritesViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getFavoritesUseCase: GetFavoritesUseCase

    @Mock
    private lateinit var uiStateObserver: Observer<FavoritesViewModel.UiState>

    private lateinit var favoritesViewModel: FavoritesViewModel

    private val character = CharacterFactory().create(CharacterFactory.Hero.ThreeD)

    @Before
    fun setUp() {
        favoritesViewModel = FavoritesViewModel(
            getFavoritesUseCase = getFavoritesUseCase,
            coroutinesDispatchers = mainCoroutineRule.testDispatcherProvider
        ).apply {
            state.observeForever(uiStateObserver)
        }
    }

    @Test
    fun `should notify uiState with ShowFavorites from UiState when get all returns success`() =
        runTest {
            // Arrange
            whenever(getFavoritesUseCase.invoke())
                .thenReturn(
                    flowOf(
                        listOf(
                            character
                        )
                    )
                )

            // Act
            favoritesViewModel.getAll()

            // Assert
            verify(uiStateObserver).onChanged(isA<FavoritesViewModel.UiState.ShowFavorites>())
            val result = favoritesViewModel.state.value as FavoritesViewModel.UiState.ShowFavorites

            assertEquals(
                1,
                result.favorites.size
            )
        }

    @Test
    fun `should notify uiState with ShowEmpty from UiState when get all returns empty`() =
        runTest {
            // Arrange
            whenever(getFavoritesUseCase.invoke())
                .thenReturn(
                    flowOf(
                        emptyList()
                    )
                )

            // Act
            favoritesViewModel.getAll()

            // Assert
            verify(uiStateObserver).onChanged(isA<FavoritesViewModel.UiState.ShowEmpty>())
        }

    @Test
    fun `should notify uiState with ShowEmpty from UiState when get all returns error`() =
        runTest {
            // Arrange
            whenever(getFavoritesUseCase.invoke())
                .thenThrow(
                    RuntimeException()
                )

            // Act
            favoritesViewModel.getAll()

            // Assert
            verify(uiStateObserver).onChanged(isA<FavoritesViewModel.UiState.ShowEmpty>())
        }
}