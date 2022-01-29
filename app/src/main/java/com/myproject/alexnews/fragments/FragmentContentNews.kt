package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DataNewsList
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.databinding.FragmentContentNewsBinding
import com.myproject.alexnews.model.Article


class FragmentContentNews(private val urlPage: String, val data: Article) : Fragment() {

    lateinit var binding: FragmentContentNewsBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.setTitle(R.string.News)
        setHasOptionsMenu(true)

        binding = FragmentContentNewsBinding.inflate(inflater, container, false)

        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = false

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

    override fun onDestroy() {
        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = true
        super.onDestroy()
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
        Toast.makeText(context, data.bookmarkEnable.toString(), Toast.LENGTH_SHORT).show()
        if (data.bookmarkEnable) {
            item.setIcon(R.drawable.bookmark_enable_icon_item)
            DataNewsList.dataList.add(data)
        } else {
            item.setIcon(R.drawable.bookmark_action_bar_content)
            DataNewsList.dataList.remove(data)
        }
    }
}