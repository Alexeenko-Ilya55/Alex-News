package com.myproject.alexnews.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myproject.alexnews.`object`.TABLE_NAME

import com.myproject.alexnews.model.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Query("SELECT * FROM $TABLE_NAME")
     suspend fun getAllArticles():List<Article>

    @Query("DELETE FROM $TABLE_NAME")
     suspend fun deleteAll()
}