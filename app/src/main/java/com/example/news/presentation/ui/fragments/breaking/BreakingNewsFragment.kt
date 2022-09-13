package com.example.news.presentation.ui.fragments.breaking

import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.common.makeToast
import com.example.news.data.local.entities.asExternalModel
import com.example.news.databinding.FragmentBreakingNewsBinding
import com.example.news.presentation.adapters.paging.BreakingNewsPagingAdapter
import com.example.news.presentation.adapters.paging.NewsPagingLoadStateAdapter
import com.example.news.presentation.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BreakingNewsFragment : BaseFragment() {

    private val TAG = "BreakingNewsFragment"

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var telephonyManager: TelephonyManager

    private lateinit var newsAdapter: BreakingNewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.breakingNewsPaging.collectLatest { pagingData ->
                    newsAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
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
        _binding = null
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        newsAdapter = BreakingNewsPagingAdapter {
            val action =
                BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleActivity(
                    it.asExternalModel()
                )
            findNavController().navigate(action)
        }
        binding.rvBreakingNews.apply {
            adapter = newsAdapter.withLoadStateHeaderAndFooter(
                header = NewsPagingLoadStateAdapter { newsAdapter.retry() },
                footer = NewsPagingLoadStateAdapter { newsAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}