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
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentMyNewsBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentMyNewsViewModel
import kotlinx.coroutines.flow.collectLatest

class FragmentMyNews : Fragment() {

    private lateinit var binding: FragmentMyNewsBinding
    private lateinit var adapter: RecyclerAdapter
    lateinit var viewModel: FragmentMyNewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle(R.string.app_name)
        binding = FragmentMyNewsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FragmentMyNewsViewModel::class.java]
        refreshInit()
        viewModel.loadNews(requireArguments(),requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.news.collectLatest {
                init(it)
            }
        }
        return binding.root
    }

    private fun refreshInit() {
        binding.refresh.setColorSchemeResources(R.color.purple_200, R.color.purple_700)
        binding.refresh.setOnRefreshListener {
            viewModel.refresh()
            binding.refresh.isRefreshing = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister: List<Article>) {
            binding.rcView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager, requireContext())
            binding.rcView.adapter = adapter
    }





}
