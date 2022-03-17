package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentSearchBinding
import com.myproject.alexnews.viewModels.FragmentSearchViewModel
import com.myproject.repository.model.Article
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentSearch : Fragment() {

    lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().setTitle(R.string.Search)
        val viewModel = ViewModelProvider(this)[FragmentSearchViewModel::class.java]
        setHasOptionsMenu(true)
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        lifecycleScope.launchWhenStarted {
            viewModel.news.collectLatest {
                init(it)
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                binding.searchView.clearFocus()
                viewModel.setInquiry(searchQuery, requireContext())
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        return binding.root
    }

    private fun init(dataLister: List<Article>) {
        lifecycleScope.launch {
            binding.apply {
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = RecyclerAdapter(
                    dataLister,
                    parentFragmentManager,
                    lifecycleScope
                )
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_search, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}