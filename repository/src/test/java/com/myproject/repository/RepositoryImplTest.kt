package com.myproject.repository

import android.content.SharedPreferences
import com.myProject.domain.models.Article
import com.myproject.repository.`object`.AUTOMATIC_DOWNLOAD
import com.myproject.repository.`object`.OFFLINE_MODE
import com.myproject.repository.api.ApiNewsRepository
import com.myproject.repository.model.ArticleEntity
import com.myproject.repository.room.RoomNewsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class RepositoryImplTest {

    private val apiRepository = mock<ApiNewsRepository>()
    private val roomRepository = mock<RoomNewsRepository>()
    private val sharedPreferences = mock<SharedPreferences>()

    private val repositoryImpl = RepositoryImpl(
        roomRepository,
        apiRepository,
        sharedPreferences
    )

    @AfterEach
    fun tearDown() {
        Mockito.reset(apiRepository, roomRepository, sharedPreferences)
    }

    @Test
    fun `should return results of search from the apiRepository`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()
        `when`(
            apiRepository.searchNews(
                searchQuery = "Search Query",
                pageIndex = 1,
                pageSize = 0
            )
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.searchNews(
            searchQuery = "Search Query",
            pageIndex = 1,
            pageSize = 0
        )

        Assertions.assertEquals(expected, actual)
        verify(apiRepository, times(1)).searchNews(
            searchQuery = "Search Query",
            pageIndex = 1,
            pageSize = 0
        )
    }

    @Test
    fun `should return results of search from sources`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()
        `when`(
            apiRepository.loadNewsFromSources(
                sourceName = "Source name",
                pageIndex = 1,
                pageSize = 0
            )
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.searchNewsFromSources(
            nameSource = "Source name",
            pageIndex = 1,
            pageSize = 0
        )

        Assertions.assertEquals(expected, actual)
        verify(apiRepository, times(1)).loadNewsFromSources(
            sourceName = "Source name",
            pageIndex = 1,
            pageSize = 0
        )
    }

    @Test
    fun `should return notes from the apiRepository`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()
        `when`(
            apiRepository.getNotes()
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.getNewsNotes()

        Assertions.assertEquals(expected, actual)
        verify(apiRepository, times(1)).getNotes()
    }

    @Test
    fun `should return bookmarks with offline mod`() = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(true)

        `when`(
            roomRepository.getBookmarks()
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.getNewsBookmarks()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return bookmarks with online mod`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(false)

        `when`(
            apiRepository.getBookmarks()
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.getNewsBookmarks()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should take news from room repository offline mod`(): Unit = runBlocking {
        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(true)
        `when`(
            roomRepository.getBookmarks()
        ).thenReturn(emptyList())

        repositoryImpl.getNewsBookmarks()

        verify(apiRepository, Mockito.never()).getBookmarks()
        verify(roomRepository, Mockito.times(1)).getBookmarks()
    }

    @Test
    fun `should take news from api repository online mod`(): Unit = runBlocking {
        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(false)
        `when`(
            apiRepository.getBookmarks()
        ).thenReturn(emptyList())

        repositoryImpl.getNewsBookmarks()

        verify(roomRepository, Mockito.never()).getBookmarks()
        verify(apiRepository, Mockito.times(1)).getBookmarks()
    }

    @Test
    fun `should take news only from room repository offline mod`(): Unit = runBlocking {
        val news = Article()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(true)

        repositoryImpl.updateElement(news)

        verify(apiRepository, Mockito.never()).updateElement(toArticleEntity(news))
        verify(roomRepository, Mockito.times(1))
            .updateElement(toArticleEntity(news))
    }

    @Test
    fun `should take news only from api repository online mod`(): Unit = runBlocking {
        val news = Article()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(false)

        repositoryImpl.updateElement(news)

        verify(roomRepository, Mockito.never()).updateElement(toArticleEntity(news))
        verify(apiRepository, Mockito.times(1))
            .updateElement(toArticleEntity(news))
    }

    @Test
    fun `should return news offline mode`() = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(true)
        `when`(
            roomRepository.getAllArticles(
                limit = 20,
                offset = 0
            )
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.getNews(positionViewPager = 0, pageIndex = 0, pageSize = 20)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return news and download online mod`() = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(false)

        `when`(
            sharedPreferences.getBoolean(AUTOMATIC_DOWNLOAD, false)
        ).thenReturn(true)

        `when`(
            apiRepository.loadNews(
                positionViewPager = 0,
                pageSize = 20,
                pageIndex = 0
            )
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.getNews(positionViewPager = 0, pageIndex = 0, pageSize = 20)

        Assertions.assertEquals(expected, actual)
        verify(roomRepository, Mockito.times(1)).insert(listArticleEntity)
        verify(roomRepository, Mockito.times(1)).deleteAll()
    }

    @Test
    fun `should return news online mod`() = runBlocking {
        val expected = emptyList<Article>()
        val listArticleEntity = emptyList<ArticleEntity>()

        `when`(
            sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ).thenReturn(false)

        `when`(
            sharedPreferences.getBoolean(AUTOMATIC_DOWNLOAD, false)
        ).thenReturn(false)

        `when`(
            apiRepository.loadNews(
                positionViewPager = 0,
                pageSize = 20,
                pageIndex = 0
            )
        ).thenReturn(listArticleEntity)

        val actual = repositoryImpl.getNews(positionViewPager = 0, pageIndex = 0, pageSize = 20)

        Assertions.assertEquals(expected, actual)
        verify(roomRepository, Mockito.never()).insert(listArticleEntity)
        verify(roomRepository, Mockito.never()).deleteAll()
    }
}