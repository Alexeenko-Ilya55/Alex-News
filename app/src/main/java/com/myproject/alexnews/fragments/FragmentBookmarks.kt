package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.myProject.domain.models.Article
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.paging.PagingAdapter
import com.myproject.alexnews.viewModels.FragmentBookmarksViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentBookmarks : Fragment() {

    lateinit var binding: FragmentBookmarksBinding
    private val viewModel: FragmentBookmarksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        requireActivity().setTitle(R.string.Bookmark)
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        lifecycleScope.launchWhenStarted {
            viewModel.loadNews().collectLatest {
                initAdapter(it)
            }
        }
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
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}