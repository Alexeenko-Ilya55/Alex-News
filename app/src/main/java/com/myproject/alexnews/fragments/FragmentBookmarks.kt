package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.ARTICLE_LIST
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentBookmarksViewModel
import kotlinx.coroutines.flow.collectLatest

class FragmentBookmarks : Fragment() {

    lateinit var binding: FragmentBookmarksBinding

    private lateinit var adapter: RecyclerAdapter
    lateinit var sp: SharedPreferences
    private lateinit var viewModel: FragmentBookmarksViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        setHasOptionsMenu(true)
        requireActivity().setTitle(R.string.Bookmark)
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FragmentBookmarksViewModel::class.java]
        if (sp.getBoolean(OFFLINE_MODE, false))
            initAdapter(ARTICLE_LIST)
        else {
            viewModel.loadNews()
            lifecycleScope.launchWhenCreated {
                viewModel.sharedFlow.collectLatest {
                    initAdapter(it)
                }
            }
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initAdapter(dataList: List<Article>) {
        binding.rcViewBookmarks.layoutManager = LinearLayoutManager(context)
        adapter = RecyclerAdapter(dataList, parentFragmentManager, requireContext())
        binding.rcViewBookmarks.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}