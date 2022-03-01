package com.myproject.alexnews.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.getString
import android.provider.Settings.Secure.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.ARTICLE_LIST
import com.myproject.alexnews.`object`.DARK_MODE
import com.myproject.alexnews.`object`.Month
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.activity.MainActivity
import com.myproject.alexnews.dao.FirebaseDB
import com.myproject.alexnews.fragments.FragmentContentNews
import com.myproject.alexnews.fragments.FragmentContentNewsOffline
import com.myproject.alexnews.model.Article
import com.squareup.picasso.Picasso
import kotlin.coroutines.coroutineContext

class RecyclerAdapter(
    private val articleList: List<Article>,
    private val parentFM: FragmentManager,
    private val context: Context
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {

    lateinit var v: View
    private lateinit var auth: FirebaseAuth
    lateinit var sp: SharedPreferences

    inner class RecyclerHolder(item: View) : RecyclerView.ViewHolder(item) {
        val context = item.context
        val title = item.findViewById<TextView>(R.id.heading)
        val time = item.findViewById<TextView>(R.id.time)
        val imageNews = item.findViewById<ImageView>(R.id.imageNews)
        val bookmarks = item.findViewById<ImageButton>(R.id.Bookmark_Item_Button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        auth = Firebase.auth
        auth.currentUser
        return RecyclerHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.apply {
            val data = articleList[position]
            val db = FirebaseDB()
            sp = PreferenceManager.getDefaultSharedPreferences(context)
            fillDataInItem(holder, data)

            bookmarks.setOnClickListener {
                data.bookmarkEnable = !data.bookmarkEnable
                if(sp.getBoolean(OFFLINE_MODE,false)){
                    if (data.bookmarkEnable)
                        ARTICLE_LIST.add(data)
                    else
                        ARTICLE_LIST.remove(data)
                }
                else{
                    if (data.bookmarkEnable) db.addToFirebase(data)
                    else db.deleteFromFB(data.url)
                }
                notifyDataSetChanged()
            }

            itemView.setOnClickListener {
                if (sp.getBoolean(OFFLINE_MODE, false)) {
                    openFragment(FragmentContentNewsOffline(data))
                } else
                    openFragment(FragmentContentNews(data))
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun fillDataInItem(holder: RecyclerHolder, data: Article) {
        holder.apply {
            val ps = PreferenceManager.getDefaultSharedPreferences(context!!)
            title.text = data.title.substringBeforeLast('-')
            time.text = data.publishedAt.substringAfterLast('-')
                .substringBefore('T') + " " +
                    month(
                        data.publishedAt.substringAfter('-').substringBeforeLast('-')
                    ) + " в " + data.publishedAt.substring(11).substringBeforeLast(':')

            if (data.urlToImage != null && data.urlToImage != "")
                Picasso.get().load(data.urlToImage).into(imageNews)
            else
                imageNews.setImageResource(R.drawable.no_image)

            if (data.bookmarkEnable)
                bookmarks.setImageResource(R.drawable.bookmark_enable_icon_item)
            else {
                if (ps.getBoolean(DARK_MODE,true))
                    bookmarks.setImageResource(R.drawable.bookmark_action_bar_content)
                else
                    bookmarks.setImageResource(R.drawable.item_bookmark)
            }
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(){
        notifyDataSetChanged()
    }

    private fun openFragment(fragment: Fragment) {
        parentFM.beginTransaction().addToBackStack(null)
            .replace(R.id.fragment_container, fragment).commit()
    }

    private fun month(monthNumber: String): String {
        when (monthNumber.toInt()) {
            Month.january.index -> return context.getString(R.string.january)
            Month.february.index -> return context.getString(R.string.february)
            Month.march.index -> return context.getString(R.string.march)
            Month.april.index -> return context.getString(R.string.april)
            Month.may.index -> return context.getString(R.string.may)
            Month.june.index -> return context.getString(R.string.june)
            Month.july.index -> return context.getString(R.string.july)
            Month.august.index -> return context.getString(R.string.august)
            Month.september.index -> return context.getString(R.string.september)
            Month.october.index -> return context.getString(R.string.october)
            Month.november.index -> return context.getString(R.string.november)
            Month.december.index -> return context.getString(R.string.december)
        }
        return context.getString(R.string.error)
    }

}