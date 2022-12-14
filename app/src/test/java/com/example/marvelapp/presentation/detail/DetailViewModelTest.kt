package com.example.marvelapp.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.base.ResultStatus
import com.example.marvelapp.R
import com.example.marvelapp.getOrAwaitValue
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharacterFactory
import com.example.testing.model.ComicFactory
import com.example.testing.model.EventFactory
import com.nhaarman.mockitokotlin2.any
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
class DetailViewModelTest {

    /*
    * Rule fornecida pelas dependencias de testes do liveData
    * Basicamente ele troca a thread em background para thread main, permitindo a execução de testes
    * de forma sincrona
    * */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase

    @Mock
    private lateinit var uiStateObserver: Observer<DetailViewModel.UiState>

    private lateinit var detailViewModel: DetailViewModel

    private val character = CharacterFactory().create(CharacterFactory.Hero.ThreeD)
    private val comics = listOf(
        ComicFactory().create(ComicFactory.FakeComic.FakeComic1)
    )
    private val events = listOf(
        EventFactory().create(EventFactory.FakeEvent.FakeEvent1)
    )

    /*
    * observeForever() faz com que toda vez que no código de produção o liveData receba um valor
    * o meu observer mockado, vinculado a esse liveData vai receber uma cópia do mesmo valor
    * */
    @Before
    fun setUp() {
        detailViewModel = DetailViewModel(getCharacterCategoriesUseCase)
        detailViewModel.uiState.observeForever(uiStateObserver)
    }


    @Test
    fun `should notify uiState with Success from UiState when get character categories returns success`() =
        runTest {
            // Arrange
            whenever(getCharacterCategoriesUseCase.invoke(any())).thenReturn(
                flowOf(
                    ResultStatus.Success(
                        comics to events
                    )
                )
            )

            // Act
            detailViewModel.getCharacterCategories(character.id)

            // Assert
            val expected = DetailViewModel.UiState.Success(
                listOf(
                    DetailParentVE(
                        categoryStringResId = R.string.details_comics_category,
                        detailChildList = listOf(
                            DetailChildVE(
                                comics[0].id,
                                comics[0].imageUrl
                            )
                        )
                    ),
                    DetailParentVE(
                        categoryStringResId = R.string.details_events_category,
                        detailChildList = listOf(
                            DetailChildVE(
                                events[0].id,
                                events[0].imageUrl
                            )
                        )
                    )
                )
            )
            val result = detailViewModel.uiState.getOrAwaitValue()
            assertEquals(expected, result)
        }

    @Test
    fun `should notify uiState with Success from UiState when get character categories returns only comics`() {
        // TODO: Implement tests
    }

    @Test
    fun `should notify uiState with Success from UiState when get character categories returns only events`() {
        // TODO: Implement tests
    }

    @Test
    fun `should notify uiState with Empty from UiState when get character categories returns an empty result list`() {
        // TODO: Implement tests
    }

    @Test
    fun `should notify uiState with Error from UiState when get character categories returns an exception`() {
        // TODO: Implement tests
    }

}