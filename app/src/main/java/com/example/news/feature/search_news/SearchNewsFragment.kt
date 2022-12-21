package com.example.news.feature.search_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.R
import com.example.news.core.common.collectLatestLifecycleFlow
import com.example.news.core.common.makeToast
import com.example.news.databinding.FragmentSearchNewsBinding
import com.example.news.feature.adapters.paging.adapter.NewsPagingLoadStateAdapter
import com.example.news.feature.adapters.paging.adapter.SearchNewsPagingAdapter
import com.example.news.feature.common.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: SearchNewsPagingAdapter

    private val viewModel: NewsViewModel by navGraphViewModels(R.id.home_nested_graph) {
        defaultViewModelProviderFactory
    }

    private val TAG = "SearchNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
//        debouncingTextListener()

        binding.etSearch.addTextChangedListener { searchQuery ->
            viewModel.saveSearchQuery(searchQuery.toString().trim())
        }

        collectLatestLifecycleFlow(viewModel.searchResult) { searchResult ->
            newsAdapter.submitData(searchResult)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && newsAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                binding.rvSearchNews.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                binding.paginationProgressBar.isVisible =
                    loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                binding.retryButton.isGone = loadState.source.refresh !is LoadState.Error

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
        _binding = null
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        newsAdapter = SearchNewsPagingAdapter {
            val action =
                SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleActivity(it)
            findNavController().navigate(action)
        }
        binding.rvSearchNews.apply {
            adapter = newsAdapter.withLoadStateHeaderAndFooter(
                header = NewsPagingLoadStateAdapter { newsAdapter.retry() },
                footer = NewsPagingLoadStateAdapter { newsAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

//    private fun debouncingTextListener() {
//        var job: Job? = null
//        binding.etSearch.addTextChangedListener { editable ->
//            job?.cancel()
//            job = MainScope().launch {
//                delay(500L)
//                editable?.let {
//                    if (editable.toString().isNotEmpty()) {
////                        viewModel.searchNews(editable.toString())
//                        viewLifecycleOwner.lifecycleScope.launch {
//                            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                                viewModel.searchNews(editable.toString())
//                                    .collectLatest { pagingData ->
//                                        newsAdapter.submitData(pagingData)
//                                    }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

}