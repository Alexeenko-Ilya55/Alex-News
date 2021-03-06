package com.myproject.alexnews.adapter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.myProject.domain.models.Article
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DARK_MODE
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.fragments.FragmentContentNews
import com.myproject.alexnews.fragments.FragmentContentNewsOffline
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecyclerAdapter(
    private val newsList: List<Article>,
    private val fragmentManager: FragmentManager,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val sharedPreferences: SharedPreferences
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {

    inner class RecyclerHolder(item: View) : RecyclerView.ViewHolder(item) {
        val context = item.context!!
        val title = item.findViewById<TextView>(R.id.heading)!!
        val time = item.findViewById<TextView>(R.id.time)!!
        val imageNews = item.findViewById<ImageView>(R.id.imageNews)!!
        val bookmarks = item.findViewById<ImageButton>(R.id.Bookmark_Item_Button)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return RecyclerHolder(view)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.apply {
            val news = newsList[position]
            fillDataInItem(holder, news)

            bookmarks.setOnClickListener {
                news.bookmarkEnable = !news.bookmarkEnable
                lifecycleScope.launch(Dispatchers.IO) {

                }
                notifyDataSetChanged()
            }

            itemView.setOnClickListener {
                if (sharedPreferences.getBoolean(OFFLINE_MODE, false)) {
                    openFragment(FragmentContentNewsOffline(news))
                } else
                    openFragment(FragmentContentNews(news))
            }

        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun fillDataInItem(holder: RecyclerHolder, news: Article) {
        holder.apply {
            title.text = news.title.substringBeforeLast('-')
            time.text = formatDate(news.publishedAt)
            if (news.urlToImage != "")
                Picasso.get().load(news.urlToImage).into(imageNews)
            else
                imageNews.setImageResource(R.drawable.no_image)
            if (news.bookmarkEnable)
                bookmarks.setImageResource(R.drawable.bookmark_enable_icon_item)
            else {
                if (sharedPreferences.getBoolean(DARK_MODE, true))
                    bookmarks.setImageResource(R.drawable.bookmark_action_bar_content)
                else
                    bookmarks.setImageResource(R.drawable.item_bookmark)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(publishedAt: String): String {
        val formatInputDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatOutputDate = SimpleDateFormat("dd MMMM HH:mm")
        formatInputDate.timeZone = TimeZone.getTimeZone("UTC")
        val docDate = formatInputDate.parse(publishedAt)
        return formatOutputDate.format(docDate!!)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    private fun openFragment(fragment: Fragment) =
        fragmentManager.beginTransaction().addToBackStack(null)
            .replace(R.id.fragment_container, fragment).commit()
}