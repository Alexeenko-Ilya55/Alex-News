package com.myproject.alexnews.di

import com.myProject.domain.useCases.*
import org.koin.dsl.module

val domainModule = module {
    single { SearchNewsUseCase(get()) }
    single { GetBookmarksUseCase(get()) }
    single { BookmarkEnableUseCase(get()) }
    single { GetNewsUseCase(get()) }
    single { GetNewsFromSourcesUseCase(get()) }
    single { GetNotesUseCase(get()) }
}
