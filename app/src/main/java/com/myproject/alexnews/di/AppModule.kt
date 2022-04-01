package com.myproject.alexnews.di

import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.adapter.ViewPagerAdapter
import com.myproject.alexnews.paging.*
import com.myproject.alexnews.viewModels.*
import io.ktor.http.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { FragmentBookmarksViewModel(get()) }
    viewModel { FragmentContentNewsOfflineViewModel(get()) }
    viewModel { FragmentContentNewsViewModel(get()) }
    viewModel { FragmentMyNewsViewModel() }
    viewModel { FragmentNewsFromSourcesViewModel() }
    viewModel { FragmentNotesViewModel(get()) }
    viewModel { FragmentOfflineViewModel() }
    viewModel { FragmentSearchViewModel() }

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidContext()) }

    single { ContextCompat.getMainExecutor(androidContext()) }
    single {
        androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle(androidContext().getString(R.string.Biometric_auth))
            .setNegativeButtonText(androidContext().getString(R.string.InputYourPassword))
            .build()
    }
    factory { params ->
        PagingAdapter(
            get(),
            get(),
            fragmentManager = params.get(),
            lifecycleScope = params.get()
        )
    }
    factory { params ->
        RecyclerAdapter(
            newsList = params.get(),
            fragmentManager = params.get(),
            lifecycleScope = params.get(),
            get()
        )
    }
    single { AlertDialog.Builder(androidContext()) }

    single {
        Firebase.auth
    }
    single { params ->
        ViewPagerAdapter(
            fragment = params.get()
        )
    }

    single {
        GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(androidContext().getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    factory { params -> MyPagingSource(params.get(), get()) }
    single { PagingBookmarksSource(get()) }
    factory { params -> PagingSearchNews(params.get(), get()) }
    factory { params -> PagingNewsFromSource(params.get(), get()) }
}