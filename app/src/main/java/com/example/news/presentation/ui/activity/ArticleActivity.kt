package com.example.news.presentation.ui.activity

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.example.news.common.makeToast
import com.example.news.common.observeOnce
import com.example.news.data.model.asEntitySavedArticle
import com.example.news.databinding.ActivityArticleBinding
import com.example.news.presentation.ui.ArticleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityArticleBinding
    private val binding get() = _binding

    private val viewModel: ArticleViewModel by viewModels()
    private val navArg: ArticleActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(navArg.article.url)
        }

        binding.fab.setOnClickListener {
            saveArticle()
        }

    }

    private fun saveArticle() {
        viewModel.getSavedNews().observeOnce(this) { savedNews ->
            if (savedNews.filter { it.url == navArg.article.url }.isNotEmpty()) {
                makeToast("Article Saved, repeated")
            } else {
                viewModel.saveArticle(navArg.article.asEntitySavedArticle())
                makeToast("Article Saved")
            }
        }
    }
}