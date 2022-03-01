package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentNewFromSourcesBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentNewsFromSourcesViewModel


class FragmentNewsFromSources : Fragment() {

    lateinit var binding: FragmentNewFromSourcesBinding
    private lateinit var adapter: RecyclerAdapter
    private lateinit var viewModel: FragmentNewsFromSourcesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewFromSourcesBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.fromSource)
        viewModel = ViewModelProvider(this)[FragmentNewsFromSourcesViewModel::class.java]

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("FragmentLiveDataObserve")
            override fun onQueryTextSubmit(sourceName: String?): Boolean {
                binding.searchView.clearFocus()
                viewModel.setInquiry(sourceName,context)
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        viewModel.news.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            init(it)
        })
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister: List<Article>) {
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager,requireContext())
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }


}
