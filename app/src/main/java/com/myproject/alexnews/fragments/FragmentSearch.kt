package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

class FragmentSearch : Fragment() {

    lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.Search)
        val viewModel = ViewModelProvider(this)[FragmentSearchViewModel::class.java]
        setHasOptionsMenu(true)
        if (viewModel.news != PagingData.empty<Article>())
            initAdapter(viewModel.news)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                binding.searchView.clearFocus()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.searchNews(searchQuery, requireContext()).collectLatest {
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
            val adapter = PagingAdapter(
                parentFragmentManager,
                lifecycleScope
            )
            binding.recyclerView.adapter = adapter
            adapter.submitData(news)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_search, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}