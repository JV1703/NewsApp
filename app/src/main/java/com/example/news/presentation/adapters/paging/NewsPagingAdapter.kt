package com.example.news.presentation.adapters.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.news.R
import com.example.news.data.network.models.NetworkArticle
import com.example.news.databinding.ArticleViewHolderBinding

class NewsPagingAdapter(private val clickListener: (NetworkArticle) -> Unit) :
    PagingDataAdapter<NetworkArticle, NewsPagingAdapter.ArticleViewHolder>(DiffUtilCallback) {

    class ArticleViewHolder(private val binding: ArticleViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: NetworkArticle) {
            binding.apply {
                ivArticleImage.load(article.urlToImage) {
                    crossfade(600)
                    error(R.drawable.ic_error_placeholder)
                }
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
                tvSource.text = article.source.name

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticleViewHolder(ArticleViewHolderBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = getItem(position)
        currentArticle?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener { clickListener(currentArticle) }
        }
    }

    object DiffUtilCallback : DiffUtil.ItemCallback<NetworkArticle>() {
        override fun areItemsTheSame(oldItem: NetworkArticle, newItem: NetworkArticle): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: NetworkArticle, newItem: NetworkArticle): Boolean {
            return oldItem == newItem
        }

    }
}