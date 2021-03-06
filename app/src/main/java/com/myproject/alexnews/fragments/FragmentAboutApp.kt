package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentAboutAppBinding

class FragmentAboutApp : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAboutAppBinding.inflate(inflater, container, false).apply {
        requireActivity().setTitle(R.string.menu_about_app)
    }.root
}