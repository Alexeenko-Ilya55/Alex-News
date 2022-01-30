package com.myproject.alexnews.fragments

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myproject.alexnews.R
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.databinding.FragmentNotesBinding

class FragmentNotes : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentNotesBinding.inflate(inflater, container, false).apply {
        activity!!.setTitle(R.string.menu_Notes)
        // Inflate the layout for this fragment

    }.root
}