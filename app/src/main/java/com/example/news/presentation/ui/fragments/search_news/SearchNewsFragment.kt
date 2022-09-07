package com.example.news.presentation.ui.fragments.search_news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.common.NetworkResult
import com.example.news.common.makeToast
import com.example.news.data.local.entities.asExternalModel
import com.example.news.data.network.models.asEntitySavedArticle
import com.example.news.databinding.FragmentSearchNewsBinding
import com.example.news.presentation.adapters.recycler_view.NewsAdapter
import com.example.news.presentation.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : BaseFragment() {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter

    private val TAG = "SearchNewsFragment"

//    private val viewModel: NewsViewModel by viewModels()

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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchNews
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect {
                        binding.paginationProgressBar.isVisible = it
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchNews.collect { uiState ->
                    when (uiState.data) {
                        is NetworkResult.Success -> {
                            newsAdapter.submitList(uiState.data.data?.networkArticles?.map {
                                it.asEntitySavedArticle().asExternalModel()
                            })
                        }
                        is NetworkResult.Error -> {
                            uiState.data.message?.let { message ->
                                Log.e(TAG, message)
                                Log.i(TAG, "network error message toast")
                                makeToast(message)
                            }
                        }
                        else -> {
                            if (!uiState.isLoading && binding.etSearch.text.isNotEmpty()) {
                                Log.i(TAG, "uistate message toast")
                                makeToast(uiState.message)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter {
            val action =
                SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleActivity(it)
            findNavController().navigate(action)
        }
        binding.rvSearchNews.apply {
            adapter = newsAdapter
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
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }
    }

}