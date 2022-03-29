package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myProject.domain.models.Article
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentNotesBinding
import com.myproject.alexnews.viewModels.FragmentNotesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentNotes : Fragment() {

    lateinit var binding: FragmentNotesBinding
    private val viewModel: FragmentNotesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        requireActivity().setTitle(R.string.menu_Notes)
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        viewModel.loadNews()
        lifecycleScope.launchWhenStarted {
            viewModel.news.collectLatest {
                initAdapter(it)
            }
        }
        return binding.root
    }

    private fun initAdapter(newsList: List<Article>) {
        lifecycleScope.launch {
            binding.rcView.layoutManager = LinearLayoutManager(context)
            val adapter: RecyclerAdapter by inject {
                parametersOf(newsList, parentFragmentManager, lifecycleScope)
            }
            binding.rcView.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}