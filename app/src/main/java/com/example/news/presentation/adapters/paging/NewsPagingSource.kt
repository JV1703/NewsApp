package com.example.news.presentation.adapters.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.news.common.networkResultHandler
import com.example.news.data.network.models.NetworkArticle
import com.example.news.data.repository.NewsRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

const val NETWORK_PAGE_SIZE = 5

class NewsPagingSource @Inject constructor(
    private val repository: NewsRepository,
    private val countryCode: String
) : PagingSource<Int, NetworkArticle>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkArticle> {
        val page = params.key ?: 1

        return try {
            val response =
                repository.getBreakingNews(countryCode, page = page, pageSize = params.loadSize)
            val result = networkResultHandler(response)

            Log.i(
                "paging_source",
                "params key: ${params.key}, params load size:${params.loadSize}, add: ${params.loadSize / NETWORK_PAGE_SIZE}, network size: ${result.data?.networkArticles?.size}, isEmpty: ${result.data?.networkArticles?.isEmpty() == true}"
            )

            val nextKey =
                if (result.data?.networkArticles?.isEmpty() == true) null else page + (params.loadSize / NETWORK_PAGE_SIZE)

            LoadResult.Page(
                data = result.data?.networkArticles ?: listOf(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NetworkArticle>): Int? {
        Log.i(
            "paging_source", "getRefreshKey triggered, return: ${
                state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }"
        )
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}