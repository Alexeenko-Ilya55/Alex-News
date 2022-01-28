package com.myproject.alexnews.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.myproject.alexnews.R

import com.myproject.alexnews.databinding.ActivityMainBinding
import com.myproject.alexnews.fragments.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    object AB{ lateinit var mToggle: ActionBarDrawerToggle}
    lateinit var  binding: ActivityMainBinding
    lateinit var  fragmentMain: FragmentMain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentMain = FragmentMain(null)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragmentMain)
                .commit()
        }

        binding.navView.bringToFront()
        binding.navView.isVerticalScrollBarEnabled
        binding.navView.setNavigationItemSelectedListener(this)


        // refresh

//        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.refresh)
//
//        swipeRefreshLayout.setColorSchemeResources(R.color.purple_200,R.color.purple_700)
//        swipeRefreshLayout.setOnRefreshListener {
//
//            dataList.clear()
//            apiRequest("https://newsapi.org/v2/top-headlines?country=us&apiKey=26c3b8d2516d4aadaf0416e2bcb1ebb8")
//            // Initialize a new Runnable
//
//            val runnable = Runnable {
//                // Update the text view text with a random number
//                Toast.makeText(this, "Страница обновлена",Toast.LENGTH_SHORT).show()
//
//                // Hide swipe to refresh icon animation
//                swipeRefreshLayout.isRefreshing = false
//            }
//            // Execute the task after specified time
//            Handler().postDelayed(
//                runnable, 2000.toLong()
//            )
//        }



        // Menu ActionBar
        //drawerLayout = findViewById(R.id.drawer_layout)
        AB.mToggle = ActionBarDrawerToggle(this,binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(AB.mToggle)
        AB.mToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(AB.mToggle.onOptionsItemSelected(item))
            return true
        when(item.itemId){
            R.id.menuSettings -> openFragment(FragmentSettings())
            R.id.change_myNews -> openFragment(FragmentChangeMyNews())
            R.id.menuBookmarks -> openFragment(FragmentBookmarks())
            R.id.menuSearch -> openFragment(FragmentSearch())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuSearch -> {
               openFragment(FragmentSearch())
            }
            R.id.NewsSources -> openFragment(FragmentNewsFromSources())
            R.id.menuNews -> openFragment(FragmentMain(null))
            R.id.menuNotes -> openFragment(FragmentNotes())
            R.id.menuBookmarks-> openFragment(FragmentBookmarks())
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
            R.id.messageProgrammer -> openFragment(FragmentMessegetoProgrammer())
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}