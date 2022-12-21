package com.example.news.feature.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.news.databinding.FragmentArticleBinding
import com.example.news.feature.ui.activity.ArticleActivityArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private val navArg: ArticleActivityArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
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