package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.databinding.FragmentOfflineBinding
import com.myproject.alexnews.paging.PagingAdapter
import com.myproject.alexnews.viewModels.FragmentOfflineViewModel
import com.myproject.repository.model.Article
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FragmentOffline : Fragment() {

    private lateinit var binding: FragmentOfflineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOfflineBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[FragmentOfflineViewModel::class.java]
        viewModel.loadNews(requireContext())
        lifecycleScope.launch {
            viewModel.loadNews(requireContext()).collect {
                init(it)
            }
        }
        return binding.root
    }

    private fun init(news: PagingData<Article>) {
        lifecycleScope.launch {
            binding.apply {
                rcView.layoutManager = LinearLayoutManager(context)
                val adapter = PagingAdapter(
                    parentFragmentManager,
                    lifecycleScope
                )
                rcView.adapter = adapter
                adapter.submitData(news)
            }
        }
    }
}