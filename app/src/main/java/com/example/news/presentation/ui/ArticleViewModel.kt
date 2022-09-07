package com.example.news.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(private val newsRepository: NewsRepository) :
    ViewModel() {

    fun saveArticle(article: EntitySavedArticle) =
        viewModelScope.launch { newsRepository.upsert(article) }

    fun getSavedNews() = newsRepository.getSavedNews().asLiveData()

}