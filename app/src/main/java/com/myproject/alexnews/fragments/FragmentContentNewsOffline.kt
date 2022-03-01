package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.ARTICLE_LIST
import com.myproject.alexnews.`object`.Month
import com.myproject.alexnews.databinding.FragmentContentNewsOfflineBinding
import com.myproject.alexnews.model.Article
import com.squareup.picasso.Picasso


class FragmentContentNewsOffline(private val data: Article) : Fragment() {

    lateinit var binding : FragmentContentNewsOfflineBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentNewsOfflineBinding.inflate(inflater,container,false)
        requireActivity().setTitle(R.string.News)
        setHasOptionsMenu(true)

        binding.apply{
            titleNews.text = data.title
            publishedAtNews.text =data.publishedAt.substringAfterLast('-')
                .substringBefore('T') + " " +
                    month(
                        data.publishedAt.substringAfter('-').substringBeforeLast('-')
                    ) + " в " + data.publishedAt.substring(11).substringBeforeLast(':')

            descriptionNews.text = data.description

            if (data.urlToImage != "")
                Picasso.get().load(data.urlToImage).into(imageNews)
            else
                imageNews.setImageResource(R.drawable.no_image)
        }
        return binding.root
    }
     private fun month(monthNumber: String): String {
        when (monthNumber.toInt()) {
            Month.january.index -> return getString(R.string.january)
            Month.february.index -> return getString(R.string.february)
            Month.march.index -> return getString(R.string.march)
            Month.april.index -> return getString(R.string.april)
            Month.may.index -> return getString(R.string.may)
            Month.june.index -> return getString(R.string.june)
            Month.july.index -> return getString(R.string.july)
            Month.august.index -> return getString(R.string.august)
            Month.september.index -> return getString(R.string.september)
            Month.october.index -> return getString(R.string.october)
            Month.november.index -> return getString(R.string.november)
            Month.december.index -> return getString(R.string.december)
        }
        return getString(R.string.error)
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
            ARTICLE_LIST.add(data)
        } else {
            item.setIcon(R.drawable.bookmark_action_bar_content)
            ARTICLE_LIST.remove(data)
        }
    }

}