package com.example.news.presentation.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.news.presentation.ui.NewsViewModel

open class BaseFragment : Fragment() {

    protected val viewModel: NewsViewModel by viewModels()

}