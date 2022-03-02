package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
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
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentSearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class FragmentSearch : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: RecyclerAdapter
    lateinit var viewModel: FragmentSearchViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle(R.string.Search)
        viewModel = ViewModelProvider(this)[FragmentSearchViewModel::class.java]
        setHasOptionsMenu(true)
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.searchView.clearFocus()
                viewModel.setInquiry(p0,context)
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                // TODO:
                return false
            }
        })
        lifecycleScope.launchWhenStarted{
            viewModel.news.collectLatest {
                init(it)
            }
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister: List<Article>) {
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager,requireContext())
            recyclerView.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_search, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}