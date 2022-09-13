package com.example.news.presentation.adapters.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.news.common.Constants
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntitySearchArticles
import com.example.news.data.network.models.asEntitySearchArticles
import com.example.news.data.repository.NewsRepository
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SearchNewsRemoteMediator @Inject constructor(
    private val query: String,
    private val repository: NewsRepository
) : RemoteMediator<Int, EntitySearchArticles>() {

    var loadTimes = 0
    var counter = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EntitySearchArticles>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getClosestRemoteKeys(state)
                remoteKey?.next?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKey?.prev
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                counter += state.config.pageSize
//                counter += 100
                val nextKey = remoteKey?.next
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        return try {
            val response = repository.searchNews(
                query = query,
                page = page,
                pageSize = state.config.pageSize
            )
            val endOfPagination = if (response.body()?.totalResults!! <= 100) {
                response.body()?.networkArticles?.size!! < state.config.pageSize
            } else {
                counter >= 100
            }

            val prev = if (page == 1) null else page - 1
            val next = if (endOfPagination) null else page + 1

            Log.i(
                "search_paging_mediator",
                "load type: ${loadType}, end of pagination: ${endOfPagination}, TEST: ${counter >= 100}, test 2: ${response.body()?.totalResults!! <= 100}, test 3: ${response.body()?.networkArticles?.size!! < state.config.pageSize}, current page: ${page}, next page: ${next}, prev page: ${prev}, response size: ${response.body()?.networkArticles?.size!!}, page size: ${state.config.pageSize}"
            )

            Log.i(
                "search_paging_mediator",
                "initial load size: ${state.config.initialLoadSize}, load size: ${state.config.pageSize}, load type: ${loadType}, end of pagination: ${endOfPagination}, totalResults: ${response.body()?.totalResults!!}, counter: ${counter}, counter >= 100: ${counter >= 100}, endOfPagination true: ${response.body()?.totalResults!! < state.config.pageSize}, endOfPagination false: ${counter >= 100}"
            )

            if (response.isSuccessful) {
                response.body()?.let {

                    //flush data
                    if (loadType == LoadType.REFRESH) {
                        repository.deleteAllSearchedArticles()
                        repository.clearRemoteKeys(Constants.KeyCategory.SEARCH)
                    }

                    val list = response.body()?.networkArticles?.map {
                        EntityArticleRemoteKey(
                            it.url,
                            prev,
                            next,
                            Constants.KeyCategory.SEARCH
                        )
                    }

                    // make list of remote keys
                    if (list != null) {
                        repository.insertAllRemoteKey(list)
                    }

                    // insert to ROOM
                    repository.insertAllSearchArticles(it.networkArticles.map {
                        it.asEntitySearchArticles()
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

    private suspend fun getClosestRemoteKeys(state: PagingState<Int, EntitySearchArticles>): EntityArticleRemoteKey? {
        Log.i(
            "search_paging_mediator",
            "closest page: ${state.anchorPosition?.let { state.closestItemToPosition(it) }}, closest item: ${
                state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.let {
                        repository.remoteKeysArticleId(
                            Constants.KeyCategory.SEARCH,
                            it.url
                        )
                    }
                }
            }"
        )

        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.let {
                repository.remoteKeysArticleId(Constants.KeyCategory.SEARCH, it.url)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, EntitySearchArticles>): EntityArticleRemoteKey? {
        Log.i(
            "search_paging_mediator",
            "last page: ${state.pages.lastOrNull { it.data.isNotEmpty() }} last item: ${
                state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                    ?.let {
                        repository.remoteKeysArticleId(
                            Constants.KeyCategory.SEARCH,
                            it.url
                        )
                    }
            }"
        )
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repository.remoteKeysArticleId(Constants.KeyCategory.SEARCH, it.url) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, EntitySearchArticles>): EntityArticleRemoteKey? {
        Log.i(
            "search_paging_mediator",
            "first page: ${state.pages.firstOrNull { it.data.isNotEmpty() }} first item: ${
                state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                    ?.let { repo ->
                        repository.remoteKeysArticleId(
                            Constants.KeyCategory.SEARCH,
                            repo.url
                        )
                    }
            }"
        )
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                repository.remoteKeysArticleId(Constants.KeyCategory.SEARCH, repo.url)
            }
    }

}