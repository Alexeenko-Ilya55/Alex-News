package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.POSITION_VIEW_PAGER
import com.myproject.alexnews.`object`.Page
import com.myproject.alexnews.adapter.ViewPagerAdapter
import com.myproject.alexnews.databinding.FragmentMainBinding
import kotlin.properties.Delegates


class FragmentMain : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private var positionViewPager by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewPagerAdapter = ViewPagerAdapter(context as FragmentActivity)
        positionViewPager = requireArguments().getInt(POSITION_VIEW_PAGER)
        requireActivity().setTitle(R.string.app_name)
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = Page.COUNT_OF_SCREEN_PAGE_LIMIT.index

        val tabNames: Array<String> = arrayOf(
            getString(R.string.menu_category_MyNews),
            getString(R.string.menu_category_Technologies),
            getString(R.string.menu_category_Sport),
            getString(R.string.menu_category_Business),
            getString(R.string.menu_category_Global),
            getString(R.string.menu_category_Health),
            getString(R.string.menu_category_Science),
            getString(R.string.menu_category_Entertainment)
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (positionViewPager != 0) binding.viewPager.setCurrentItem(positionViewPager, true)
    }

    fun navigationViewPager(positionViewPager: Int) {
        if (this.isVisible)
            binding.viewPager.setCurrentItem(positionViewPager, true)
        else {
            val fragment = FragmentMain()
            fragment.arguments = Bundle().apply {
                putInt(POSITION_VIEW_PAGER, positionViewPager)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()
        }
    }
}






