package com.myproject.alexnews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.databinding.FragmentNotesBinding
import com.myproject.alexnews.model.Str

class FragmentNotes : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentNotesBinding.inflate(inflater,container,false).apply {
        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = false
        // Inflate the layout for this fragment
        val url = Str.URL_START+Str.COUNTRY+"&"+Str.CATEGORY_SPORTS+Str.API_KEY

    }.root
    override fun onDestroy() {
        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = true
        super.onDestroy()
    }

}