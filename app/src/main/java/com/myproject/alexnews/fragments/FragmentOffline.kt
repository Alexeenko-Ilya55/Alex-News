package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.dao.AppDataBase
import com.myproject.alexnews.dao.ArticleRepositoryImpl
import com.myproject.alexnews.databinding.FragmentOfflineBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentBookmarksViewModel
import com.myproject.alexnews.viewModels.FragmentOfflineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentOffline : Fragment() {

    private lateinit var binding: FragmentOfflineBinding
    private lateinit var adapter: RecyclerAdapter
    private lateinit var viewModel : FragmentOfflineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOfflineBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[FragmentOfflineViewModel::class.java]
        viewModel.loadNews()
        viewModel.news.observe(viewLifecycleOwner, Observer {
            init(it)
        })
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister:List<Article>) {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager,requireContext())
            rcView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}