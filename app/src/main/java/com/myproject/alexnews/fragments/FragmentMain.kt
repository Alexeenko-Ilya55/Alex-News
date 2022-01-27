package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.ViewPagerAdapter
import com.myproject.alexnews.databinding.FragmentMainBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import org.jetbrains.anko.doAsync
import java.text.FieldPosition


class FragmentMain(val myPosition: Int?) : Fragment() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var binding: FragmentMainBinding
    private val tabNames: Array<String> = arrayOf(
        "Мои новости",
        "Технологии",
        "Спорт",
        "Бизнес",
        "Мировые",
        "Здоровье",
        "Наука",
        "Развлечения"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentMainBinding.inflate(inflater, container, false)

        viewPagerAdapter = ViewPagerAdapter(context as FragmentActivity)
        // binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = 7

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(myPosition!=null) binding.viewPager.setCurrentItem(myPosition,true)

    }
     fun navVP(pos: Int){
         if(this.isVisible)
        binding.viewPager.setCurrentItem(pos,true)
         else {
             parentFragmentManager.beginTransaction().replace(R.id.fragment_container,FragmentMain(pos)).commit()

         }
     }
}






