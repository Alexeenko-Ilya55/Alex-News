package com.myproject.alexnews.fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragment
import com.myproject.alexnews.viewModels.FragmentBookmarksViewModel
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

class FragmentBookmarksTest{

    @Test
    fun testBookmarkFragment() {
        val viewModel = mockk<FragmentBookmarksViewModel>()
        val fragmentArgs = bundleOf()
        launchFragment<FragmentBookmarks>(fragmentArgs)

        every { viewModel.loadNews() } returns emptyFlow()
        verify(exactly = 1) { viewModel.loadNews() }
        confirmVerified(viewModel)
    }
}