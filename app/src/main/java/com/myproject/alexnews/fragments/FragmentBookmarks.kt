package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentBookmarksBinding
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentBookmarks() : Fragment() {

    private var dataList: MutableList<Article> = mutableListOf()
    lateinit var binding: FragmentBookmarksBinding

    private lateinit var adapter: RecyclerAdapter
    lateinit var sp:SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sp = PreferenceManager.getDefaultSharedPreferences(context!!)
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

    @SuppressLint("NotifyDataSetChanged")
    fun initAdapter(dataList: MutableList<Article>){
        lifecycleScope.launch(Dispatchers.Main) {
            binding.rcViewBookmarks.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataList, parentFragmentManager)
            binding.rcViewBookmarks.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.group_bookmsark, false)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun takeFromFB(){
        Log.d("MyLog",sp.getBoolean("OfflineMode", false).toString())
        if(sp.getBoolean("OfflineMode", false))
            initAdapter(ARTICLE_LIST)
        else {
            lifecycleScope.launch(Dispatchers.IO) {
                val aList: MutableList<Article> = mutableListOf()
                val auth = Firebase.auth
                auth.currentUser
                REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (aList.size != 0) aList.clear()
                            snapshot.children.forEach {
                                aList.add(it.getValue(Article::class.java)!!)
                            }
                            initAdapter(aList)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("MyLog", "Database Error")
                        }
                    }
                    )
            }
        }
    }
}