package com.example.marvelapp.presentation.characters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelapp.databinding.ItemCharacterLoadMoreStateBinding

class CharactersLoadMoreStateViewHolder(
    itemCharacterLoadMoreStateBinding: ItemCharacterLoadMoreStateBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(itemCharacterLoadMoreStateBinding.root) {

    private val binding = ItemCharacterLoadMoreStateBinding.bind(itemView)
    private val progressBarLoadingMore = binding.progressLoadMore
    private val textTryAgainMessage = binding.textTryAgain.also {
        it.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        progressBarLoadingMore.isVisible = loadState is LoadState.Loading
        textTryAgainMessage.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): CharactersLoadMoreStateViewHolder {
            val itemBinding = ItemCharacterLoadMoreStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return CharactersLoadMoreStateViewHolder(itemBinding, retry)
        }
    }
}