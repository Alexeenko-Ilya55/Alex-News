package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.*
import android.webkit.WebViewClient
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DataNewsList
import com.myproject.alexnews.`object`.NODE_USERS
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.dao.FirebaseDB
import com.myproject.alexnews.databinding.FragmentContentNewsBinding
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentContentNews(private val urlPage: String, val data: Article) : Fragment() {

    lateinit var binding: FragmentContentNewsBinding
    private lateinit var auth: FirebaseAuth
    private val  db = FirebaseDB()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity!!.setTitle(R.string.News)
        setHasOptionsMenu(true)
        auth = Firebase.auth
        auth.currentUser


        binding = FragmentContentNewsBinding.inflate(inflater, container, false)

        binding.apply {
            WebView.webViewClient = WebViewClient()
            WebView.apply {
                WebView.loadUrl(urlPage)
                settings.safeBrowsingEnabled = true
                settings.javaScriptEnabled = true
                WebView.doOnAttach { }
            }

            floatingActionButton.setOnClickListener {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT, urlPage)
                    this.type = "text/plain"
                }
                startActivity(shareIntent)
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_content_news_menu, menu)
        menu.setGroupVisible(R.id.group_bookmsark, false)
        if (data.bookmarkEnable) {
            menu.setGroupVisible(R.id.Enable, true)
            menu.setGroupVisible(R.id.notEnable, false)
        } else {
            menu.setGroupVisible(R.id.notEnable, true)
            menu.setGroupVisible(R.id.Enable, false)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuBookmarks_content)
            clickOnBookmark(item)
        if (item.itemId == R.id.menuBookmarks_content_enable)
            clickOnBookmark(item)
        return true
    }

    private fun clickOnBookmark(item: MenuItem) {
        data.bookmarkEnable = !data.bookmarkEnable
        if (data.bookmarkEnable) {
            item.setIcon(R.drawable.bookmark_enable_icon_item)
            db.addToFirebase(data)
        } else {
            item.setIcon(R.drawable.bookmark_action_bar_content)
            db.deleteFromFB(data.url)
        }
    }

    override fun onDestroy() {


        super.onDestroy()
    }
}