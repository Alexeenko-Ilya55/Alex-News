package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DataNewsList
import com.myproject.alexnews.`object`.NODE_USERS
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.`object`.Settings
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.dao.FirebaseDB
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentBookmarks() : Fragment() {

    private var dataList: MutableList<Article> = mutableListOf()
    lateinit var binding: FragmentBookmarksBinding

    private lateinit var adapter: RecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        activity!!.setTitle(R.string.Bookmark)
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        bdRequest()
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun bdRequest() {
        takeFromFB()
    }

    fun initAdapter(dataList: MutableList<Article>){
        binding.rcViewBookmarks.layoutManager = LinearLayoutManager(context)
        adapter = RecyclerAdapter(dataList, parentFragmentManager)
        binding.rcViewBookmarks.adapter = adapter
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun takeFromFB(){
        lifecycleScope.launch(Dispatchers.IO) {
            val auth = Firebase.auth
            auth.currentUser
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    val aList: MutableList<Article> = mutableListOf()
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(aList.size != 0) aList.clear()
                        snapshot.children.forEach {
                            aList.add(it.getValue(Article::class.java)!!)
                        }
                        initAdapter(aList)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("MyLog","onCancelled")
                    }
                }
            )
        }
    }
}