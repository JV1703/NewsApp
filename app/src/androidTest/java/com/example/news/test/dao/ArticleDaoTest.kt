package com.example.news.test.dao

import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.news.core.data.local.ArticleDatabase
import com.example.news.core.data.local.dao.ArticleDao
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntitySavedArticle
import com.example.news.core.test.FakeDataGenerator
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.random.Random.Default.nextInt

class TestNavHostControllerRule(
    @NavigationRes private val navigationGraph: Int,
    @IdRes private val currentDestination: Int,
    private val viewModelStore: ViewModelStore = ViewModelStore(),
) : ExternalResource() {
    lateinit var testNavHostController: TestNavHostController
        private set

    override fun before() {
        super.before()
        // Before test
        testNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())
        testNavHostController.setViewModelStore(viewModelStore)
        testNavHostController.setGraph(navigationGraph)
        testNavHostController.setCurrentDestination(currentDestination)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class ArticleDaoTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: ArticleDatabase

    @Inject
    lateinit var fakeDataGenerator: FakeDataGenerator

    private lateinit var dao: ArticleDao
    lateinit var testNavHostController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.articleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun upsertSavedArticle_insert_success() = runTest {
        val entitySavedArticle = fakeDataGenerator.generateEntitySavedArticle(1)
        dao.upsertSavedArticle(entitySavedArticle)
        val actual = dao.getSavedArticle(entitySavedArticle.url).first()
        assertEquals(entitySavedArticle, actual)
    }

    @Test
    fun upsertSavedArticle_update_success() = runTest {
        val entitySavedArticleList = arrayListOf<EntitySavedArticle>()
        for (i in 0..5) {
            entitySavedArticleList.add(fakeDataGenerator.generateEntitySavedArticle(i))
        }

        entitySavedArticleList.forEach {
            dao.upsertSavedArticle(it)
        }

        val newTitle = "New Saved Article"
        val itemToUpdate =
            entitySavedArticleList[nextInt(entitySavedArticleList.size)].copy(title = newTitle)

        dao.upsertSavedArticle(itemToUpdate)
        val cacheData = dao.getSavedArticle(itemToUpdate.url).first()

        assertEquals(itemToUpdate, cacheData)
    }

    @Test
    fun getSavedArticle() = runTest {
        val entitySavedArticleList = arrayListOf<EntitySavedArticle>()
        for (i in 0..5) {
            entitySavedArticleList.add(fakeDataGenerator.generateEntitySavedArticle(i))
        }

        entitySavedArticleList.forEach {
            dao.upsertSavedArticle(it)
        }

        val itemToRetrieve = entitySavedArticleList[nextInt(entitySavedArticleList.size)]
        val cacheData = dao.getSavedArticle(itemToRetrieve.url).first()

        assertEquals(itemToRetrieve, cacheData)
    }

    @Test
    fun deleteSavedArticle() = runTest {
        val entitySavedArticleList = arrayListOf<EntitySavedArticle>()
        for (i in 0..5) {
            entitySavedArticleList.add(fakeDataGenerator.generateEntitySavedArticle(i))
        }

        entitySavedArticleList.forEach {
            dao.upsertSavedArticle(it)
        }

        val itemToDelete = entitySavedArticleList[nextInt(entitySavedArticleList.size)]
        dao.deleteSavedArticle(itemToDelete.url)
        val cacheData = dao.getAllSavedArticles().first()

        assertNull(cacheData.find { it.url == itemToDelete.url })
    }

    @Test
    fun insertAllArticles() = runTest {
        val entityArticleList = arrayListOf<EntityArticle>()
        for (i in 0..5) {
            entityArticleList.add(fakeDataGenerator.generateEntityArticle(i))
        }

        dao.insertAllArticles(entityArticleList)
        val cacheData = dao.getAllEntityArticles().first()

        assertEquals(entityArticleList, cacheData)
    }

    @Test
    fun deleteAllArticle() = runTest {
        val entityArticleList = arrayListOf<EntityArticle>()
        for (i in 0..5) {
            entityArticleList.add(fakeDataGenerator.generateEntityArticle(i))
        }

        dao.insertAllArticles(entityArticleList)
        dao.deleteAllArticle()
        val cacheData = dao.getAllEntityArticles().first()

        assertEquals(emptyList<EntityArticle>(), cacheData)
    }

}