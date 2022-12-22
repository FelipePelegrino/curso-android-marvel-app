package com.example.marvelapp.presentation.common

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class GenericViewHolder<T>(
    itemBind: ViewBinding
) : RecyclerView.ViewHolder(itemBind.root) {

    abstract fun bind(data: T)
}