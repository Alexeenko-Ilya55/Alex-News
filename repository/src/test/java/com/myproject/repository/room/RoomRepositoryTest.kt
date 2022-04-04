package com.myproject.repository.room

import com.myproject.repository.model.ArticleEntity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class RoomRepositoryTest {
    private val articleDao = mock<ArticleDao>()
    private val roomRepository = RoomRepository(articleDao)

    @AfterEach
    fun tearDown() {
        Mockito.reset(articleDao)
    }

    @Test
    fun `should load news from room`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()

        `when`(
            articleDao.getAllArticles(20, 0)
        ).thenReturn(expected)

        val actual = roomRepository.getAllArticles(20, 0)

        Assertions.assertEquals(expected, actual)
        verify(articleDao, times(1)).getAllArticles(20, 0)
    }

    @Test
    fun `should delete all from room`(): Unit = runBlocking {

        roomRepository.deleteAll()

        verify(articleDao, times(1)).deleteAll()
    }

    @Test
    fun `should get bookmarks from room`(): Unit = runBlocking {
        val expected = emptyList<ArticleEntity>()

        `when`(
            articleDao.getBookmarks()
        ).thenReturn(expected)

        val actual = roomRepository.getBookmarks()

        Assertions.assertEquals(expected, actual)
        verify(articleDao, times(1)).getBookmarks()
    }

    @Test
    fun `should update element in room`(): Unit = runBlocking {
        val article = ArticleEntity()

        roomRepository.updateElement(article)

        verify(articleDao, times(1)).updateElement(article)
    }

    @Test
    fun `should insert list news in room`(): Unit = runBlocking {
        val newsList = emptyList<ArticleEntity>()

        roomRepository.insert(newsList)

        for (element in newsList)
            verify(articleDao, times(1)).insert(element)
    }
}