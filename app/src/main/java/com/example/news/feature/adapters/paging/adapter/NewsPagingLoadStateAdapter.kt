package com.example.news.feature.adapters.paging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.news.databinding.PagingLoadStateViewHolderBinding

class NewsPagingLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<NewsPagingLoadStateAdapter.NewsPagingFooterViewHolder>() {

    inner class NewsPagingFooterViewHolder(private val binding: PagingLoadStateViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

    }

    override fun onBindViewHolder(holder: NewsPagingFooterViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): NewsPagingFooterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NewsPagingFooterViewHolder(
            PagingLoadStateViewHolderBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }


}