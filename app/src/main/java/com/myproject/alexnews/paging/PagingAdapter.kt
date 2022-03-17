package com.myproject.alexnews.paging

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
import androidx.paging.PagingDataAdapter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DARK_MODE
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.fragments.FragmentContentNews
import com.myproject.alexnews.fragments.FragmentContentNewsOffline
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PagingAdapter(
    private val fragmentManager: FragmentManager,
    private val lifecycleScope: LifecycleCoroutineScope
) : PagingDataAdapter<Article, PagingAdapter.Holder>(UsersDiffCallback()) {

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val news = getItem(position)!!
        holder.apply {
            val repository = RepositoryImpl(context, lifecycleScope)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            fillDataInItem(holder, news)

            bookmarks.setOnClickListener {
                news.bookmarkEnable = !news.bookmarkEnable
                lifecycleScope.launch(Dispatchers.IO) {
                    repository.updateElement(news)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false))


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context!!
        val title = itemView.findViewById<TextView>(R.id.heading)!!
        val time = itemView.findViewById<TextView>(R.id.time)!!
        val imageNews = itemView.findViewById<ImageView>(R.id.imageNews)!!
        val bookmarks = itemView.findViewById<ImageButton>(R.id.Bookmark_Item_Button)!!
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun fillDataInItem(holder: Holder, news: Article) {
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

    private fun openFragment(fragment: Fragment) =
        fragmentManager.beginTransaction().addToBackStack(null)
            .replace(R.id.fragment_container, fragment).commit()
}

// ---

class UsersDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}