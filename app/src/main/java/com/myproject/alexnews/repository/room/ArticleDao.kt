package com.myproject.alexnews.repository.room

import androidx.room.*
import com.myproject.alexnews.`object`.TABLE_NAME
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllArticles(): Flow<List<Article>>

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateElement(news: Article)
}