package com.myproject.alexnews.repository.room

import androidx.room.*
import com.myproject.alexnews.`object`.TABLE_NAME
import com.myproject.alexnews.model.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Query("SELECT * FROM $TABLE_NAME LIMIT :limit OFFSET :offset")
    fun getAllArticles(limit: Int, offset: Int): List<Article>

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateElement(news: Article)
}

