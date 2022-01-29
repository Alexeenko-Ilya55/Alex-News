package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import com.myproject.alexnews.R

class FragmentSettings : PreferenceFragmentCompat() {

    @SuppressLint("RestrictedApi")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        activity!!.setTitle(R.string.menu_Settings)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)
        preferenceScreen.setOnExpandButtonClickListener {
            Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_settings, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}