package com.example.news.feature.adapters.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.news.core.common.NetworkResource
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntityArticleRemoteKey
import com.example.news.core.repository.NewsRepository
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BreakingNewsRemoteMediator @Inject constructor(
    private val repository: NewsRepository,
    private val countryCode: String,
    private val category: String?,
) : RemoteMediator<Int, EntityArticle>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EntityArticle>,
    ): MediatorResult {
        Log.e("Shit", "triggered")
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getClosestRemoteKeys(state)
                remoteKey?.next?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKey?.prev ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null)
                prevKey

            }
            LoadType.APPEND -> {
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.next ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        val networkResponse = repository.getBreakingNews(page = page,
            pageSize = state.config.pageSize,
            countryCode = countryCode,
            category = category)

        when (networkResponse) {
            is NetworkResource.Success -> {
                val articles = networkResponse.data.networkArticles
                val endOfPagination = articles.size < state.config.pageSize || articles.isEmpty()

                if (loadType == LoadType.REFRESH) {
                    repository.deleteAllArticle()
                    repository.clearRemoteKeys()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                val articleEntityList = articles.map { it }

                // make list of remote keys
                if (articleEntityList.isNotEmpty()) {
                    val keys = articleEntityList.map {
                        EntityArticleRemoteKey(it.url, prevKey, nextKey)
                    }
                    repository.insertAllRemoteKey(keys)
                    repository.insertAllArticles(articleEntityList)
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPagination)
            }
            is NetworkResource.Error -> {
                Timber.e("shit: ${networkResponse.errMsg}")
                Log.e("Shit", "${networkResponse.errMsg}")
                return MediatorResult.Error(throwable = Exception(networkResponse.errMsg))
            }
            is NetworkResource.Empty -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    }

    private suspend fun getClosestRemoteKeys(state: PagingState<Int, EntityArticle>): EntityArticleRemoteKey? {

        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.let {
                repository.remoteKeysArticleId(it.url)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, EntityArticle>): EntityArticleRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            repository.remoteKeysArticleId(it.url)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, EntityArticle>): EntityArticleRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { repo ->
            // Get the remote keys of the first items retrieved
            repository.remoteKeysArticleId(repo.url)
        }
    }
}