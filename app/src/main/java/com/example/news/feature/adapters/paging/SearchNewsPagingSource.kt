package com.example.news.feature.adapters.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.news.core.common.NetworkResource
import com.example.news.core.data.model.Article
import com.example.news.core.repository.NewsRepository
import javax.inject.Inject

class SearchNewsPagingSource @Inject constructor(
    private val query: String,
    private val repository: NewsRepository,
    private val category: String?,
    private val sortBy: String?,
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1

        return try {
            val networkResponse =
                repository.searchNews(query = query,
                    page = page,
                    pageSize = params.loadSize,
                    category = category,
                    sortBy = sortBy)

            val prevKey = if (page == 1) null else page - 1

            when (networkResponse) {

                is NetworkResource.Success -> {
                    val articles = networkResponse.data.networkArticles
                    val nextKey =
                        if (articles.size < params.loadSize || articles.isEmpty()) null else page + 1
                    LoadResult.Page(data = articles, prevKey = prevKey, nextKey = nextKey)
                }

                is NetworkResource.Error -> {
                    Log.e("PagingSource", "errMsg: ${networkResponse.errMsg}")
                    LoadResult.Error(throwable = Exception("errMsg: ${networkResponse.errMsg}"))
                }

                is NetworkResource.Empty -> {
                    LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                }

            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}


