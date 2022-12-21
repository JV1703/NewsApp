package com.example.news.feature.breaking_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.R
import com.example.news.core.common.collectLatestLifecycleFlow
import com.example.news.core.common.makeToast
import com.example.news.databinding.FragmentBreakingNewsBinding
import com.example.news.feature.adapters.paging.adapter.BreakingNewsPagingAdapter
import com.example.news.feature.adapters.paging.adapter.NewsPagingLoadStateAdapter
import com.example.news.feature.common.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: BreakingNewsPagingAdapter

    private val viewModel: NewsViewModel by navGraphViewModels(R.id.home_nested_graph) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.swipeRefreshLayout.setOnRefreshListener {
            newsAdapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        collectLatestLifecycleFlow(viewModel.breakingNewsPaging) { pagingData ->
            newsAdapter.submitData(pagingData)
        }

        collectLatestLifecycleFlow(newsAdapter.loadStateFlow) {
            newsAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && newsAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                binding.rvBreakingNews.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                binding.paginationProgressBar.isVisible =
                    loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    makeToast("\uD83D\uDE28 Wooops ${it.error}")
                }
            }
        }

        binding.retryButton.setOnClickListener { newsAdapter.retry() }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        newsAdapter = BreakingNewsPagingAdapter {
            val action =
                BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleActivity(it)
            findNavController().navigate(action)
        }
        binding.rvBreakingNews.apply {
            adapter =
                newsAdapter.withLoadStateHeaderAndFooter(header = NewsPagingLoadStateAdapter { newsAdapter.retry() },
                    footer = NewsPagingLoadStateAdapter { newsAdapter.retry() })
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}