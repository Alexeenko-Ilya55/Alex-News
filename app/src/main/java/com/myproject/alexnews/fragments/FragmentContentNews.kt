package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.R


class FragmentContentNews(private val urlPage: String) : Fragment() {

    lateinit var  webView:WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_content_news,container,false)
        // Inflate the layout for this fragment
        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = false
        webView = v.findViewById(R.id.WebView)
        webView.webViewClient = WebViewClient()
        webView.apply {
            webView.loadUrl(urlPage)
            settings.safeBrowsingEnabled = true
            settings.javaScriptEnabled = true
            //
        }
        return v
    }
    override fun onDestroy() {
        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = true
        super.onDestroy()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return true
    }
}