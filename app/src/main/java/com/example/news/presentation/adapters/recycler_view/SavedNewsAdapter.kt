package com.example.news.presentation.adapters.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.news.R
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.databinding.ArticleViewHolderBinding

class SavedNewsAdapter(private val clickListener: (EntitySavedArticle) -> Unit) :
    ListAdapter<EntitySavedArticle, SavedNewsAdapter.ArticleViewHolder>(DiffUtilCallback) {

    class ArticleViewHolder(private val binding: ArticleViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: EntitySavedArticle) {

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
        holder.bind(currentArticle)
        holder.itemView.setOnClickListener { clickListener(currentArticle) }
    }

    object DiffUtilCallback : DiffUtil.ItemCallback<EntitySavedArticle>() {
        override fun areItemsTheSame(
            oldItem: EntitySavedArticle,
            newItem: EntitySavedArticle
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: EntitySavedArticle,
            newItem: EntitySavedArticle
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun getSavedArticle(position: Int): EntitySavedArticle {
        return getItem(position)
    }
}