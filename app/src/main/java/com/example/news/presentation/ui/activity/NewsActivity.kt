package com.example.news.presentation.ui.activity

import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.news.R
import com.example.news.databinding.ActivityNewsBinding
import com.example.news.presentation.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityNewsBinding
    private val binding get() = _binding

    private lateinit var navController: NavController
    private val viewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var telephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        getCountryCode()
        setupBottomNav()
    }

    private fun getCountryCode(): String {
        val countryCodeValue = telephonyManager.networkCountryIso
        Log.i("locale_activity", "country code: $countryCodeValue")
        return countryCodeValue
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}