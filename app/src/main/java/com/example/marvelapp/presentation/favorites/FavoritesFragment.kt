package com.example.marvelapp.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.marvelapp.databinding.FragmentFavoritesBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.presentation.common.getGenericAdapterOf
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private val favoritesAdapter by lazy {
        getGenericAdapterOf {
            FavoritesViewHolder.create(
                it,
                imageLoader
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFavoritesBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFavoritesAdapter()
        setObservers()
        favoritesViewModel.getAll()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setObservers() {
        favoritesViewModel.state.observe(viewLifecycleOwner) { uiState ->
            binding.flipperFavorites.displayedChild = when (uiState) {
                FavoritesViewModel.UiState.ShowEmpty -> {
                    favoritesAdapter.submitList(emptyList())
                    FLIPPER_CHILD_EMPTY
                }
                is FavoritesViewModel.UiState.ShowFavorites -> {
                    favoritesAdapter.submitList(uiState.favorites)
                    FLIPPER_CHILD_CHARACTERS
                }
            }
        }
    }

    private fun initFavoritesAdapter() {
        binding.recyclerFavorites.run {
            setHasFixedSize(true)
            adapter = favoritesAdapter
        }
    }

    companion object {
        private const val FLIPPER_CHILD_CHARACTERS = 0
        private const val FLIPPER_CHILD_EMPTY = 1
    }
}