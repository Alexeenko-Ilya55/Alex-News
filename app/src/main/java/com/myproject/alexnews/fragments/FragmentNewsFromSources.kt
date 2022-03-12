package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentNewFromSourcesBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentNewsFromSourcesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentNewsFromSources : Fragment() {

    lateinit var binding: FragmentNewFromSourcesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewFromSourcesBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.fromSource)
        val viewModel = ViewModelProvider(this)[FragmentNewsFromSourcesViewModel::class.java]

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(sourceName: String): Boolean {
                binding.searchView.clearFocus()
                viewModel.setInquiry(sourceName, requireContext())
                lifecycleScope.launchWhenStarted {
                    viewModel.news.collectLatest {
                        init(it)
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
}
