package com.myproject.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myproject.repository.model.ArticleEntity

@Database(
    entities = [ArticleEntity::class], version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun ArticleDao(): ArticleDao

    companion object {

        fun buildsDatabase(context: Context, dbName: String): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, dbName).build()
        }
    }
}