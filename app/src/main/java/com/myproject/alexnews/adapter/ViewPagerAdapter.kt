package com.myproject.alexnews.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.myproject.alexnews.`object`.ARG_OBJECT
import com.myproject.alexnews.`object`.Page

import com.myproject.alexnews.fragments.FragmentMyNews

class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = Page.COUNT.index

    override fun createFragment(position: Int): Fragment {

        val fragment = FragmentMyNews()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position)
        }
        return fragment
    }
}