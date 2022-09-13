package com.example.news.presentation.ui.fragments.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.news.common.makeToast
import com.example.news.data.model.asEntitySavedArticle
import com.example.news.databinding.FragmentArticleBinding
import com.example.news.presentation.ui.activity.ArticleActivityArgs
import com.example.news.presentation.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : BaseFragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private val navArg: ArticleActivityArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.webView.apply {
//            webViewClient = WebViewClient()
//            settings.javaScriptEnabled = true
//            loadUrl(navArg.article.url)
//        }

//        binding.fab.setOnClickListener {
//            saveArticle()
//        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

//    private fun saveArticle() {
//        viewModel.getSavedNews().observe(viewLifecycleOwner) { savedNews ->
//            if (savedNews.filter { it.url == navArg.article.url }.isNotEmpty()) {
//                makeToast("Article Saved, repeated")
//            } else {
//                viewModel.saveArticle(navArg.article.asEntitySavedArticle())
//                makeToast("Article Saved")
//            }
//        }
//    }

}