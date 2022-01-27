package com.myproject.alexnews.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.myproject.alexnews.R
import com.myproject.alexnews.fragments.ARG_OBJECT
import com.myproject.alexnews.fragments.FragmentContentNews
import com.myproject.alexnews.fragments.FragmentMyNews
import com.myproject.alexnews.model.Article
import com.squareup.picasso.Picasso

class RecyclerAdapter(
    private val articleList: MutableList<Article>,
    private val parentFM: FragmentManager
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {

    lateinit var v: View

    inner class RecyclerHolder(item: View) : RecyclerView.ViewHolder(item) {
        val context = item.context
        val title = item.findViewById<TextView>(R.id.heading)
        val time = item.findViewById<TextView>(R.id.time)
        val imageNews = item.findViewById<ImageView>(R.id.imageNews)
        val bookmarks = item.findViewById<ImageButton>(R.id.Bookmark_Item_Button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return RecyclerHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.apply {
            //  Toast.makeText(context,articleList.size.toString(),Toast.LENGTH_SHORT).show()
            val data = articleList[position]
            title.text = data.title.substringBeforeLast('-')
            time.text = data.publishedAt.substringAfterLast('-')
                .substringBefore('T') + " " +
                    month(
                        data.publishedAt.substringAfter('-').substringBeforeLast('-')
                    ) + " в " + data.publishedAt.substring(11).substringBeforeLast(':')

            if (data.urlToImage != null)
                Picasso.get().load(data.urlToImage)
                    .into(imageNews)
            else
                imageNews.setImageResource(R.drawable.no_image)
            if (data.bookmarkEnable)
                Toast.makeText(context, "helllo epta", Toast.LENGTH_SHORT).show()

            bookmarks.setOnClickListener {
//                val fragment = FragmentMyNews()
//                fragment.arguments = Bundle().apply {
//                    putString(ARG_OBJECT, data.url)
//                }

            if(data.bookmarkEnable) {
                bookmarks.setImageResource(R.drawable.item_bookmark)

                Toast.makeText(context,"$position, ${data.bookmarkEnable}",Toast.LENGTH_SHORT).show()
                data.bookmarkEnable = false
            }
            else {
                bookmarks.setImageResource(R.drawable.bookmark_enable_icon_item)
                Toast.makeText(context,"$position, ${data.bookmarkEnable}",Toast.LENGTH_SHORT).show()
                data.bookmarkEnable = true
            }




            }
            itemView.setOnClickListener {
//                val intent = Intent(context, ContentNewsActivity::class.java).apply {
//                    putExtra("title", data.title)
//                    putExtra("imageUrl", data.urlToImage)
//                    putExtra("publishedAt", data.publishedAt)
//                    putExtra("description", data.description)
//                    putExtra("content", data.content)
//                    putExtra("author", data.author)
//                    putExtra("url",data.url)
//
//                }
//                context.startActivity(intent)

                goToContent(data)
            }

        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNews(list: MutableList<Article>) {
        articleList.clear()
        articleList.addAll(list)
        notifyDataSetChanged()
    }

    fun goToContent(data: Article) {
        val fragment = FragmentMyNews()
        fragment.arguments = Bundle().apply {
            putString(ARG_OBJECT, data.url)
        }
        parentFM.beginTransaction().addToBackStack(null)
            .replace(R.id.fragment_container, FragmentContentNews(data.url)).commit()
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
}