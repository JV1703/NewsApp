package com.example.news.presentation.adapters.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.network.models.NetworkArticle
import com.example.news.data.network.models.asEntityArticles
import com.example.news.data.repository.NewsRepository
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator @Inject constructor(
    private val repository: NewsRepository,
    private val countryCode: String
) : RemoteMediator<Int, NetworkArticle>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NetworkArticle>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.APPEND -> {
                val remoteKey = getLastRemoteKey(state)
                val nextKey = remoteKey?.next
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.REFRESH -> {
                val remoteKey = getClosestRemoteKeys(state)
                remoteKey?.next?.minus(1) ?: 1
            }
        }

        return try {
            val response = repository.getBreakingNews(
                page = page,
                pageSize = state.config.pageSize,
                countryCode = countryCode
            )
            val endOfPagination = response.body()?.networkArticles?.size!! < state.config.pageSize

            if (response.isSuccessful) {
                response.body()?.let {

                    //flush data
                    if (loadType == LoadType.REFRESH) {
                        repository.deleteAllArticle()
                        repository.clearRemoteKeys()
                    }

                    val prev = if (page == 1) null else page - 1
                    val next = if (endOfPagination) null else page + 1

                    val list = response.body()?.networkArticles?.map {
                        EntityArticleRemoteKey(it.url, prev, next)
                    }

                    // make list of remote keys
                    if (list != null) {
                        repository.insertAllRemoteKey(list)
                    }

                    // insert to ROOM
                    repository.insertAllArticles(it.networkArticles.map {
                        it.asEntityArticles()
                    })
                }
                MediatorResult.Success(endOfPagination)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }

    private suspend fun getClosestRemoteKeys(state: PagingState<Int, NetworkArticle>): EntityArticleRemoteKey? {
        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.let {
                repository.remoteKeysArticleId(it.url)
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, NetworkArticle>): EntityArticleRemoteKey? {
        return state.lastItemOrNull()?.let {
            Log.i("paging_last", "entityArticle: ${repository.remoteKeysArticleId(it.url)}")
            repository.remoteKeysArticleId(it.url)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, NetworkArticle>): EntityArticleRemoteKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                repository.remoteKeysArticleId(repo.url)
            }
    }

}