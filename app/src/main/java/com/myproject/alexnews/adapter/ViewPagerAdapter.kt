package com.myproject.alexnews.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.myproject.alexnews.fragments.ARG_OBJECT
import com.myproject.alexnews.fragments.FragmentMyNews

class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 8

    override fun createFragment(position: Int): Fragment {

        val fragment = FragmentMyNews()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position)
        }
        return fragment
    }

}