package com.example.news.feature.adapters.paging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.news.R
import com.example.news.core.data.model.Article
import com.example.news.databinding.ArticleViewHolderBinding

class BreakingNewsPagingAdapter(private val clickListener: (Article) -> Unit) :
    PagingDataAdapter<Article, BreakingNewsPagingAdapter.ArticleViewHolder>(DiffUtilCallback) {

    class ArticleViewHolder(private val binding: ArticleViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
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

    object DiffUtilCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }
}