package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentSearchBinding
import com.myproject.alexnews.paging.PagingAdapter
import com.myproject.alexnews.viewModels.FragmentSearchViewModel
import com.myproject.repository.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentSearch : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val viewModel: FragmentSearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.Search)
        setHasOptionsMenu(true)
        if (viewModel.news != PagingData.empty<Article>())
            initAdapter(viewModel.news)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                binding.searchView.clearFocus()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.searchNews(searchQuery).collectLatest {
                        initAdapter(it)
                        viewModel.news = it
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_search, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}