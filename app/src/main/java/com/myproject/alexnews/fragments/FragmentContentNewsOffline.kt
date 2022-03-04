package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myproject.alexnews.R
import com.myproject.alexnews.databinding.FragmentContentNewsOfflineBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentContentNewsOfflineViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class FragmentContentNewsOffline(private val news: Article) : Fragment() {

    lateinit var binding: FragmentContentNewsOfflineBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentNewsOfflineBinding.inflate(inflater, container, false)
        requireActivity().setTitle(R.string.News)
        setHasOptionsMenu(true)

        binding.apply {
            titleTextView.text = news.title
            publishedAtTextView.text = formatDate(news.publishedAt)
            descriptionTextView.text = news.description
            if (news.urlToImage != "")
                Picasso.get().load(news.urlToImage).into(imageNews)
            else
                imageNews.setImageResource(R.drawable.no_image)
        }
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(publishedAt: String): String {
        val formatInputDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatOutputDate = SimpleDateFormat("dd MMMM HH:mm")
        formatInputDate.timeZone = TimeZone.getTimeZone("UTC")
        val docDate = formatInputDate.parse(publishedAt)
        return formatOutputDate.format(docDate!!)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_content_news_menu, menu)
        menu.setGroupVisible(R.id.group_bookmsark, false)
        if (news.bookmarkEnable) {
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
        val viewModel = ViewModelProvider(this)[FragmentContentNewsOfflineViewModel::class.java]
        news.bookmarkEnable = !news.bookmarkEnable
        if (news.bookmarkEnable) {
            item.setIcon(R.drawable.bookmark_enable_icon_item)
            viewModel.updateElementInDatabase(news, requireContext())
        } else {
            item.setIcon(R.drawable.bookmark_action_bar_content)
            viewModel.updateElementInDatabase(news, requireContext())
        }
    }
}