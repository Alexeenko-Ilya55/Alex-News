package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.doOnAttach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentContentNewsBinding


class FragmentContentNews(private val urlPage: String) : Fragment() {

    lateinit var binding: FragmentContentNewsBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentContentNewsBinding.inflate(inflater,container,false)

        MainActivity.AB.mToggle.isDrawerIndicatorEnabled = false

        binding.apply {
            WebView.webViewClient = WebViewClient()
            WebView.apply {
                WebView.loadUrl(urlPage)
                settings.safeBrowsingEnabled = true
                settings.javaScriptEnabled = true
                WebView.doOnAttach {  }
            }

            floatingActionButton.setOnClickListener {

                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT,urlPage)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return true
    }
}