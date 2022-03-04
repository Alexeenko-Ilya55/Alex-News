package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentBookmarksViewModel
import kotlinx.coroutines.flow.collectLatest

class FragmentBookmarks : Fragment() {

    lateinit var binding: FragmentBookmarksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        requireActivity().setTitle(R.string.Bookmark)
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[FragmentBookmarksViewModel::class.java]
        viewModel.loadNews(requireContext())
        lifecycleScope.launchWhenCreated {
            viewModel.news.collectLatest {
                initAdapter(it)
            }
        }
        return binding.root
    }

    private fun initAdapter(newsList: List<Article>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val recyclerAdapter =
            RecyclerAdapter(newsList, parentFragmentManager, requireContext(), lifecycleScope)
        binding.recyclerView.adapter = recyclerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}