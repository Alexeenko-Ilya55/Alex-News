package com.myproject.alexnews.adapter

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.fragments.ARG_OBJECT
import com.myproject.alexnews.fragments.FragmentMyNews
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi

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