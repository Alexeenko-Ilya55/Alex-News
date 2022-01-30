package com.myproject.alexnews.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.myproject.alexnews.R

import com.myproject.alexnews.databinding.ActivityMainBinding
import com.myproject.alexnews.fragments.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mToggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var fragmentMain: FragmentMain

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.bringToFront()
        binding.navView.isVerticalScrollBarEnabled
        binding.navView.setNavigationItemSelectedListener(this)

        when (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("DarkMode", false)) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

        // Menu ActionBar
        mToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        super.onCreate(savedInstanceState)

        fragmentMain = FragmentMain(null)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, FragmentSearch())
                .commit()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mToggle.onOptionsItemSelected(item))
            return true
        when (item.itemId) {
            R.id.menuSettings -> openFragment(FragmentSettings())
            R.id.change_myNews -> openFragment(FragmentChangeMyNews())
            R.id.menuBookmarks -> openFragment(FragmentBookmarks())
            R.id.menuSearch -> openFragment(FragmentSearch())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                openFragment(FragmentSearch())
            }
            R.id.NewsSources -> openFragment(FragmentNewsFromSources())
            R.id.menuNews -> openFragment(FragmentMain(null))
            R.id.menuNotes -> openFragment(FragmentNotes())
            R.id.menuBookmarks -> openFragment(FragmentBookmarks())
            R.id.menuSettings -> openFragment(FragmentSettings())
            R.id.categoryGlobal -> fragmentMain.navVP(4)
            R.id.categoryBusiness -> fragmentMain.navVP(3)
            R.id.categoryHealth -> fragmentMain.navVP(5)
            R.id.categoryMyNews -> fragmentMain.navVP(0)
            R.id.categoryEntertainment -> fragmentMain.navVP(7)
            R.id.categoryScience -> fragmentMain.navVP(6)
            R.id.categorySport -> fragmentMain.navVP(2)
            R.id.categoryTechnologies -> fragmentMain.navVP(1)
            R.id.aboutApp -> openFragment(FragmentAboutApp())
            R.id.messageProgrammer -> openFragment(FragmentMessageToProgrammer())
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences?, key: String?) {
        if (key == "DarkMode") {
            when (sp?.getBoolean("DarkMode", false)) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

}