package com.example.news.feature.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.news.R
import com.example.news.core.common.collectLatestLifecycleFlow
import com.example.news.core.common.network_connectivity_observer.ConnectivityObserver
import com.example.news.databinding.ActivityNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    private lateinit var _binding: ActivityNewsBinding
    private val binding get() = _binding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        collectLatestLifecycleFlow(connectivityObserver.observe()) { connectionStatus ->
            if (connectionStatus == ConnectivityObserver.Status.Available) {
                binding.connectionStatus.isGone = true
            } else {
                binding.connectionStatus.isGone = false
                binding.connectionStatus.text = "Connection ${connectionStatus.name}"
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}