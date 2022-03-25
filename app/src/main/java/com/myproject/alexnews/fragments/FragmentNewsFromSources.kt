package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentNewFromSourcesBinding
import com.myproject.alexnews.paging.PagingAdapter
import com.myproject.alexnews.viewModels.FragmentNewsFromSourcesViewModel
import com.myproject.repository.model.Article
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FragmentNewsFromSources : Fragment() {

    lateinit var binding: FragmentNewFromSourcesBinding
    private val viewModel: FragmentNewsFromSourcesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewFromSourcesBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.fromSource)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(sourceName: String): Boolean {
                binding.searchView.clearFocus()
                lifecycleScope.launch {
                    viewModel.newsFromSources(sourceName).collectLatest {
                        initAdapter(it)
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return binding.root
    }

    private fun initAdapter(news: PagingData<Article>) {
        lifecycleScope.launch {
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter: PagingAdapter by inject {
                parametersOf(
                    parentFragmentManager,
                    lifecycleScope
                )
            }
            binding.recyclerView.adapter = adapter
            adapter.submitData(news)
        }
    }
}
