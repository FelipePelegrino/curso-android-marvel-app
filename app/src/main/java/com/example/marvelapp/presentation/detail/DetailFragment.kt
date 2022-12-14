package com.example.marvelapp.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.marvelapp.databinding.FragmentDetailBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding get() = _binding!!

    private val args by navArgs<DetailFragmentArgs>()

    private val viewModel: DetailViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val detailViewArg = args.detailViewArg
        setTransition(detailViewArg)
        setSharedElementTransitionOnEnter()
        setObservables(detailViewArg.characterId)
        viewModel.getCharacterCategories(detailViewArg.characterId)
    }

    private fun setTransition(detailViewArg: DetailViewArg) {
        binding.imageCharacter.run {
            transitionName = detailViewArg.name
            imageLoader.load(
                imageView = this,
                imageUrl = detailViewArg.imageUrl
            )
        }
    }

    private fun setObservables(characterId: Int) {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState?.run {
                handleUiState(this, characterId)
            }
        }
    }

    private fun handleUiState(
        uiState: DetailViewModel.UiState,
        characterId: Int
    ) {
        binding.flipperDetail.displayedChild = when (uiState) {
            DetailViewModel.UiState.Loading -> FLIPPER_CHILD_POSITION_LOADING
            is DetailViewModel.UiState.Success -> {
                binding.recyclerParentDetail.run {
                    setHasFixedSize(true)
                    adapter = DetailParentAdapter(uiState.detailParentList, imageLoader)
                }

                FLIPPER_CHILD_POSITION_DETAIL
            }
            DetailViewModel.UiState.Error -> {
                binding.includeErrorView.buttonRetry.setOnClickListener {
                    viewModel.getCharacterCategories(characterId)
                }
                FLIPPER_CHILD_POSITION_ERROR
            }
            DetailViewModel.UiState.Empty -> FLIPPER_CHILD_POSITION_EMPTY
        }
    }

    private fun setSharedElementTransitionOnEnter() {
        TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                sharedElementEnterTransition = this
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FLIPPER_CHILD_POSITION_LOADING = 0
        private const val FLIPPER_CHILD_POSITION_DETAIL = 1
        private const val FLIPPER_CHILD_POSITION_ERROR = 2
        private const val FLIPPER_CHILD_POSITION_EMPTY = 3
    }

}