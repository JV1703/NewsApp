package com.example.news.presentation.adapters.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.news.common.Constants
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.network.models.asEntityArticles
import com.example.news.data.repository.NewsRepository
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BreakingNewsRemoteMediator @Inject constructor(
    private val repository: NewsRepository,
    private val countryCode: String
) : RemoteMediator<Int, EntityArticles>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EntityArticles>
    ): MediatorResult {

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
                val prevKey = remoteKey?.prev
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                prevKey

            }
            LoadType.APPEND -> {
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.next
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        return try {
            val response = repository.getBreakingNews(
                page = page,
                pageSize = state.config.pageSize,
                countryCode = countryCode
            )
            val endOfPagination = response.body()?.networkArticles?.size!! < state.config.pageSize

            val prev = if (page == 1) null else page - 1
            val next = if (endOfPagination) null else page + 1

            Log.i(
                "break_paging_mediator",
                "current page: ${page}, next page: ${next}, prev page: ${prev}, response size: ${response.body()?.networkArticles?.size!!}, page size: ${state.config.pageSize}"
            )

            Log.i(
                "break_paging_mediator",
                "state: ${state.anchorPosition}, load type: ${loadType}, current page: ${page}, next page: ${next}, prev page: ${prev}, response size: ${response.body()?.networkArticles?.size!!}, page size: ${state.config.pageSize}"
            )

            if (response.isSuccessful) {
                response.body()?.let {

                    //flush data
                    if (loadType == LoadType.REFRESH) {
                        repository.deleteAllArticle()
                        repository.clearRemoteKeys(Constants.KeyCategory.BREAKING_NEWS)
                    }

                    val list = response.body()?.networkArticles?.map {
                        EntityArticleRemoteKey(
                            it.url,
                            prev,
                            next,
                            Constants.KeyCategory.BREAKING_NEWS
                        )
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
                MediatorResult.Success(endOfPaginationReached = endOfPagination)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }

    private suspend fun getClosestRemoteKeys(state: PagingState<Int, EntityArticles>): EntityArticleRemoteKey? {
        Log.i(
            "break_paging_mediator",
            "closest page: ${state.anchorPosition?.let { state.closestItemToPosition(it) }}, closest item: ${
                state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.let {
                        repository.remoteKeysArticleId(
                            Constants.KeyCategory.BREAKING_NEWS,
                            it.url
                        )
                    }
                }
            }"
        )

        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.let {
                repository.remoteKeysArticleId(Constants.KeyCategory.BREAKING_NEWS, it.url)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, EntityArticles>): EntityArticleRemoteKey? {
        Log.i(
            "break_paging_mediator",
            "last page: ${state.pages.lastOrNull { it.data.isNotEmpty() }} last item: ${
                state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                    ?.let {
                        repository.remoteKeysArticleId(
                            Constants.KeyCategory.BREAKING_NEWS,
                            it.url
                        )
                    }
            }"
        )
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repository.remoteKeysArticleId(Constants.KeyCategory.BREAKING_NEWS, it.url) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, EntityArticles>): EntityArticleRemoteKey? {
        Log.i(
            "break_paging_mediator",
            "first page: ${state.pages.firstOrNull { it.data.isNotEmpty() }} first item: ${
                state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                    ?.let { repo ->
                        repository.remoteKeysArticleId(
                            Constants.KeyCategory.BREAKING_NEWS,
                            repo.url
                        )
                    }
            }"
        )
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                repository.remoteKeysArticleId(Constants.KeyCategory.BREAKING_NEWS, repo.url)
            }
    }
}