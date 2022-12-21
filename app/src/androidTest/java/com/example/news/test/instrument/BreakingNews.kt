package com.example.news.test.instrument

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.news.R
import com.example.news.feature.adapters.recycler_view.SavedNewsAdapter
import com.example.news.feature.saved_news.SavedNewsFragment
import com.example.news.launchFragmentInHiltContainer
import com.example.news.test.dao.TestNavHostControllerRule
import com.example.news.util.MainCoroutineRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class BreakingNews {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule(order = 3)
    var testNavHostControllerRule =
        TestNavHostControllerRule(navigationGraph = R.navigation.news_nav_graph,
            currentDestination = R.id.savedNewsFragment)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun breakingNews_loadData() = runTest {

        launchFragmentInHiltContainer<SavedNewsFragment>(navHostController = testNavHostControllerRule.testNavHostController) {

            onView(withId(R.id.rvSavedNews)).perform(RecyclerViewActions.scrollTo<SavedNewsAdapter.ArticleViewHolder>(
                hasDescendant(withText("Scarlet"))))

        }

    }


}