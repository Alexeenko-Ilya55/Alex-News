package com.myproject.alexnews.fragments

import com.myproject.alexnews.viewModels.FragmentNotesViewModel
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class FragmentNotesTest : KoinComponent {

    private val viewModel: FragmentNotesViewModel by inject()

    @Test
    fun getFlowFromViewModel() {


    }
}