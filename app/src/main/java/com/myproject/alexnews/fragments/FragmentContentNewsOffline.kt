package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.ARTICLE_LIST
import com.myproject.alexnews.databinding.FragmentContentNewsOfflineBinding
import com.myproject.alexnews.model.Article
import com.squareup.picasso.Picasso


class FragmentContentNewsOffline(val data: Article) : Fragment() {

    lateinit var binding : FragmentContentNewsOfflineBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentNewsOfflineBinding.inflate(inflater,container,false)
        activity!!.setTitle(R.string.News)
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
            1 -> return "января"
            2 -> return "февраля"
            3 -> return "марта"
            4 -> return "апреля"
            5 -> return "мая"
            6 -> return "июня"
            7 -> return "июля"
            8 -> return "августа"
            9 -> return "сентября"
            10 -> return "октября"
            11 -> return "ноября"
            12 -> return "декабря"
        }
        return "Ошибка"
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