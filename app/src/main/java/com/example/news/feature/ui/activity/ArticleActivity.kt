package com.example.news.feature.ui.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.navArgs
import com.example.news.core.common.collectLatestLifecycleFlow
import com.example.news.core.common.makeToast
import com.example.news.core.data.model.Article
import com.example.news.databinding.ActivityArticleBinding
import com.example.news.feature.article.ArticleViewModel
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

        val displayedArticle = navArg.article

        collectLatestLifecycleFlow(viewModel.getSavedArticle(displayedArticle.url)) { article: Article? ->

            binding.fab.setOnClickListener {
                if (displayedArticle == article) {
                    viewModel.deleteArticle(article.url)
                    makeToast("Article deleted")
                } else {
                    viewModel.saveArticle(displayedArticle)
                    makeToast("Article saved")
                }
            }

        }

        binding.webView.apply {

            settings.javaScriptEnabled = true
            loadUrl(displayedArticle.url)
            settings.domStorageEnabled = true

            webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)

                    if (newProgress != 100) {
                        binding.progressInd.progress = newProgress
                    } else {
                        binding.progressInd.isGone = true
                    }

                }


            }

//            webViewClient = object: WebViewClient(){
//
//                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                    binding.progressInd.isGone = false
//                    super.onPageStarted(view, url, favicon)
//                }
//
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    binding.progressInd.isGone = true
//                    super.onPageFinished(view, url)
//                }
//            }
        }

    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

            super.onPageStarted(view, url, favicon)
        }
    }
}