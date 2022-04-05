package com.myproject.alexnews.fragments
/*
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.myproject.alexnews.viewModels.FragmentBookmarksViewModel
import com.myproject.alexnews.viewModels.FragmentOfflineViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.mock


@RunWith(AndroidJUnit4::class)
class FragmentOfflineTest {

    @Test
    fun testOfflineViewModel (){
        val viewModel = mock<FragmentOfflineViewModel>()

        Mockito.`when`(viewModel.loadNews()).thenReturn(emptyFlow())

        val fragmentArgs = bundleOf()
        val scenario = launchFragment<FragmentBookmarks>(fragmentArgs)
        scenario.moveToState(Lifecycle.State.RESUMED)
        Mockito.verify(viewModel, Mockito.times(1)).loadNews()

    }

}*/