package com.myproject.alexnews.app

import android.app.Application
import com.myproject.alexnews.di.appModule
import com.myproject.alexnews.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {

        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule, repositoryModule)
        }
    }
}