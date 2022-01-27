package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DataNewsList
import com.myproject.alexnews.`object`.Settings
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.model.Article


class FragmentBookmarks() : Fragment() {

    private val dataList: MutableList<Article> = DataNewsList.dataList
    lateinit var binding:FragmentBookmarksBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarksBinding.inflate(inflater,container,false)

        binding.rcViewBookmarks.layoutManager= LinearLayoutManager(context)
        val adapter = RecyclerAdapter(dataList, parentFragmentManager)
        binding.rcViewBookmarks.adapter = adapter
        adapter.notifyDataSetChanged()

        return binding.root
    }
}