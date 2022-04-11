package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.myProject.domain.models.Article
import com.myproject.alexnews.databinding.FragmentOfflineBinding
import com.myproject.alexnews.paging.PagingAdapter
import com.myproject.alexnews.viewModels.FragmentOfflineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentOffline : Fragment() {

    private lateinit var binding: FragmentOfflineBinding
    private val viewModel: FragmentOfflineViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOfflineBinding.inflate(inflater, container, false)
        viewModel.loadNews()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadNews().collectLatest {
                initAdapter(it)
            }
        }
        return binding.root
    }

    private fun initAdapter(news: PagingData<Article>) {
        lifecycleScope.launch {
            binding.apply {
                rcView.layoutManager = LinearLayoutManager(context)
                val adapter: PagingAdapter by inject {
                    parametersOf(
                        parentFragmentManager,
                        lifecycleScope
                    )
                }
                rcView.adapter = adapter
                adapter.submitData(news)
            }
        }
    }
}