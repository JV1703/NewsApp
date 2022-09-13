package com.example.news.presentation.ui.fragments.search_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.common.makeToast
import com.example.news.data.local.entities.asExternalModel
import com.example.news.databinding.FragmentSearchNewsBinding
import com.example.news.presentation.adapters.paging.NewsPagingLoadStateAdapter
import com.example.news.presentation.adapters.paging.SearchNewsPagingAdapter
import com.example.news.presentation.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : BaseFragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: SearchNewsPagingAdapter

    private val TAG = "SearchNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        debouncingTextListener()

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
                binding.retryButton.isGone = loadState.source.refresh is LoadState.Error

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
                SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleActivity(it.asExternalModel())
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

    private fun debouncingTextListener() {
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
//                        viewModel.searchNews(editable.toString())
                        viewLifecycleOwner.lifecycleScope.launch {
                            repeatOnLifecycle(Lifecycle.State.STARTED) {
                                viewModel.searchNews(editable.toString())
                                    .collectLatest { pagingData ->
                                        newsAdapter.submitData(pagingData)

                                    }
                            }
                        }
                    }
                }
            }
        }
    }

}