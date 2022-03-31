package com.myproject.repository.api

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.myproject.repository.BuildConfig
import com.myproject.repository.`object`.*
import com.myproject.repository.api.ktor.KtorService
import com.myproject.repository.api.retrofit.ApiService
import com.myproject.repository.model.ArticleEntity
import com.myproject.repository.model.DataFromApiEntity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.collections.set


class ApiRepositoryTest {

    private val retrofit = mock<ApiService>()

    private val ktor = mock<KtorService>()
    private val sharedPreferences = mock<SharedPreferences>()
    private val auth = mock<FirebaseAuth>()

    private val apiRepositoryKtor = ApiRepository(sharedPreferences, retrofit, auth, ktor, KTOR)
    private val apiRepositoryRetrofit =
        ApiRepository(sharedPreferences, retrofit, auth, ktor, RETROFIT)

    private val options = HashMap<String, String>()

    init {
        options[COUNTRY] = "ua"
        options[CATEGORY] = CATEGORY_MY_NEWS
        options[PAGE] = "0"
        options[PAGE_SIZE] = "20"
        options[API_KEY] = BuildConfig.API_KEY
    }


    @AfterEach
    fun tearDown() {
        Mockito.reset(sharedPreferences, retrofit, ktor)
    }

    @Test
    fun `should load news from sources with retrofit`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()
        val dataFromApiEntity = DataFromApiEntity(emptyList(), "status", 0)

        `when`(
            retrofit.searchNewsFromSources(
                typeNews = EVERYTHING_NEWS,
                sourceName = "sourceName",
                pageIndex = 1,
                pageSize = 20,
                apiKey = BuildConfig.API_KEY
            )
        ).thenReturn(dataFromApiEntity)

        val actual = apiRepositoryRetrofit.loadNewsFromSources(
            "sourceName",
            1,
            20
        )

        Assertions.assertEquals(expected, actual)
        verify(retrofit, times(1)).searchNewsFromSources(
            EVERYTHING_NEWS,
            "sourceName",
            1,
            20,
            BuildConfig.API_KEY
        )
    }

    @Test
    fun `should load news from sources with ktor`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()
        val url =
            BASE_URL + "$SOURCE=sourceName&$PAGE=1" +
                    "&$PAGE_SIZE=20&$API_KEY=" + BuildConfig.API_KEY

        `when`(
            ktor.getNews(url)
        ).thenReturn(expected)

        val actual = apiRepositoryKtor.loadNewsFromSources(
            "sourceName",
            1,
            20
        )

        Assertions.assertEquals(expected, actual)
        verify(ktor, times(1)).getNews(url)
        verify(retrofit, Mockito.never()).searchNewsFromSources(
            EVERYTHING_NEWS,
            "sourceName",
            0,
            20,
            BuildConfig.API_KEY
        )
    }

    @Test
    fun `should search news with retrofit`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()
        val dataFromApiEntity = DataFromApiEntity(emptyList(), "status", 0)

        `when`(
            retrofit.searchNewsList(
                typeNews = EVERYTHING_NEWS,
                query = "searchQuery",
                pageIndex = 0,
                pageSize = 20,
                apiKey = BuildConfig.API_KEY
            )
        ).thenReturn(dataFromApiEntity)

        val actual = apiRepositoryRetrofit.searchNews(
            "searchQuery",
            0,
            20
        )

        Assertions.assertEquals(expected, actual)
        verify(retrofit, times(1)).searchNewsList(
            EVERYTHING_NEWS,
            "searchQuery",
            0,
            20,
            BuildConfig.API_KEY
        )
    }

    @Test
    fun `should search news with ktor`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()
        val url = BASE_URL + "$EVERYTHING_NEWS?q=searchQuery&$PAGE=0&" +
                "$PAGE_SIZE=20&$API_KEY=" + BuildConfig.API_KEY

        `when`(
            ktor.getNews(url)
        ).thenReturn(expected)

        val actual = apiRepositoryKtor.searchNews(
            "searchQuery",
            0,
            20
        )

        Assertions.assertEquals(expected, actual)
        verify(ktor, times(1)).getNews(url)
        verify(retrofit, Mockito.never()).searchNewsList(
            EVERYTHING_NEWS,
            "searchQuery",
            0,
            20,
            BuildConfig.API_KEY
        )
    }

    @Test
    fun `should load news with retrofit`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()
        val dataFromApiEntity = DataFromApiEntity(emptyList(), "status", 0)

        `when`(
            sharedPreferences.getString(COUNTRY, "")
        ).thenReturn("ua")

        `when`(
            retrofit.getNewsList(
                typeNews = HEADLINES_NEWS,
                options = options
            )
        ).thenReturn(dataFromApiEntity)

        val actual = apiRepositoryRetrofit.loadNews(
            0,
            0,
            20
        )

        Assertions.assertEquals(expected, actual)
        verify(retrofit, times(1)).getNewsList(
            typeNews = HEADLINES_NEWS,
            options = options
        )
    }

    @Test
    fun `should load news with ktor`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()
        val url =
            "$BASE_URL$HEADLINES_NEWS?$CATEGORY=$CATEGORY_MY_NEWS&" +
                    "$COUNTRY=ua&$PAGE=1&$PAGE_SIZE=20&$API_KEY=" + BuildConfig.API_KEY

        `when`(
            sharedPreferences.getString(COUNTRY, "")
        ).thenReturn("ua")

        `when`(
            ktor.getNews(url)
        ).thenReturn(expected)

        val actual = apiRepositoryKtor.loadNews(
            0,
            1,
            20
        )

        Assertions.assertEquals(expected, actual)
        verify(ktor, times(1)).getNews(url)
        verify(retrofit, Mockito.never()).getNewsList(
            HEADLINES_NEWS,
            options
        )
    }
}