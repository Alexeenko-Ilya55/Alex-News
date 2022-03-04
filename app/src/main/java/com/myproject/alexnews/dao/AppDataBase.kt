package com.myproject.alexnews.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myproject.alexnews.model.Article

@Database(
    entities = [Article::class], version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun ArticleDao(): ArticleDao

    companion object {

        fun buildsDatabase(context: Context, dbName: String): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, dbName).build()
        }
    }
}