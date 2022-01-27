package com.myproject.alexnews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.databinding.FragmentSettingsBinding


class FragmentSettings : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSettingsBinding.inflate(inflater, container, false).apply {


    }.root
}