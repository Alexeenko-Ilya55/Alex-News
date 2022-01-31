package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.dao.AppDataBase
import com.myproject.alexnews.dao.ArticleRepositoryImpl
import com.myproject.alexnews.databinding.FragmentOfflineBinding
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentOffline : Fragment() {

    private lateinit var binding: FragmentOfflineBinding
    private lateinit var adapter: RecyclerAdapter
    lateinit var repository: ArticleRepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOfflineBinding.inflate(inflater,container,false)
        val database = AppDataBase.buildsDatabase(context!!, DATABASE_NAME)
        repository = ArticleRepositoryImpl(database.ArticleDao())
        extractArticles()
        return binding.root
    }

    private fun extractArticles() {
        lifecycleScope.launch(Dispatchers.IO) {
            val news: MutableList<Article> = repository.getAllPersons()
            withContext(Dispatchers.Main) {
                init(news)
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister: MutableList<Article>) {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager)
            rcView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}