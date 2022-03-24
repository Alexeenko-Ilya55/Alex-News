package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentMyNewsBinding
import com.myproject.alexnews.paging.PagingAdapter
import com.myproject.alexnews.viewModels.FragmentMyNewsViewModel
import com.myproject.repository.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentMyNews : Fragment() {

    private lateinit var binding: FragmentMyNewsBinding
    private val viewModel: FragmentMyNewsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        requireActivity().setTitle(R.string.app_name)
        binding = FragmentMyNewsBinding.inflate(inflater, container, false)
        refreshInit()
        if (viewModel.news != PagingData.empty<Article>())
            initAdapter(viewModel.news)
        getData()
        return binding.root
    }

    private fun refreshInit() {
        binding.refresh.setColorSchemeResources(R.color.purple_200, R.color.purple_700)
        binding.refresh.setOnRefreshListener {
            getData()
            binding.refresh.isRefreshing = false
        }
    }

    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadNews(requireArguments()).collectLatest {
                initAdapter(it)
                viewModel.news = it
            }
        }
    }

    private fun initAdapter(news: PagingData<Article>) {
        lifecycleScope.launch {
            binding.rcView.layoutManager = LinearLayoutManager(context)
            val adapter: PagingAdapter by inject {
                parametersOf(
                    parentFragmentManager,
                    lifecycleScope
                )
            }
            binding.rcView.adapter = adapter
            adapter.submitData(news)
        }
    }
}
