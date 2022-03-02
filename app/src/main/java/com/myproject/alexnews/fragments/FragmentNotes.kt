package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.ARTICLE_LIST
import com.myproject.alexnews.`object`.NODE_USERS
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.databinding.FragmentNotesBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentNotesViewModel
import com.myproject.alexnews.viewModels.FragmentOfflineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentNotes : Fragment() {
    lateinit var binding: FragmentNotesBinding

    private lateinit var adapter: RecyclerAdapter
    private lateinit var viewModel: FragmentNotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        requireActivity().setTitle(R.string.menu_Notes)
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FragmentNotesViewModel::class.java]
        viewModel.loadNews()
        lifecycleScope.launchWhenStarted {
            viewModel.news.collectLatest {
                initAdapter(it)
            }
        }
        return binding.root
    }
    @SuppressLint("NotifyDataSetChanged")
    fun initAdapter(dataList: List<Article>){
            binding.rcView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataList, parentFragmentManager,requireContext())
            binding.rcView.adapter = adapter
            adapter.notifyDataSetChanged()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }

}