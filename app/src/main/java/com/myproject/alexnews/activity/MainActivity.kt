package com.myproject.alexnews.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.databinding.ActivityMainBinding
import com.myproject.alexnews.fragments.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var drawerMenuToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentMain: FragmentMain
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        nawViewInit()
        sharedPreferences()
        actionBarInit()
        signInAccountInit()
        initFirebase()

        fragmentMain = FragmentMain()
        fragmentMain.arguments = Bundle().apply {
            putInt(POSITION_VIEW_PAGER, Page.MY_NEWS.index)
        }
        if (savedInstanceState == null) {
            fragmentCheckOnlineMode()
        }
    }

    private fun signInAccountInit() {
        auth = Firebase.auth
        auth.currentUser
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, getString(R.string.error_authentication), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun nawViewInit() {
        binding.navView.bringToFront()
        binding.navView.isVerticalScrollBarEnabled
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun actionBarInit() {
        drawerMenuToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(drawerMenuToggle)
        drawerMenuToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun sharedPreferences() {
        when (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("DarkMode", false)) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerMenuToggle.onOptionsItemSelected(item))
            return true
        when (item.itemId) {
            R.id.accountStatus -> checkSignIn(item)
            R.id.accountStatusAuth -> checkSignIn(item)
            R.id.menuSettings -> openFragment(FragmentSettings())
            R.id.change_myNews -> openFragment(FragmentChangeMyNews())
            R.id.menuBookmarks -> openFragment(FragmentBookmarks())
            R.id.menuSearch -> openFragment(FragmentSearch())
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkSignIn(item: MenuItem) {
        if (auth.currentUser != null) {
            auth.signOut()
            item.title = getString(R.string.Auth)
            Toast.makeText(this, R.string.youLogOut, Toast.LENGTH_SHORT).show()
        } else {
            signInWithGoogle()
            item.title = getString(R.string.logOut)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (auth.currentUser != null) {
            menu.setGroupVisible(R.id.account, false)
            menu.setGroupVisible(R.id.accountAuth, true)
        } else {
            menu.setGroupVisible(R.id.account, true)
            menu.setGroupVisible(R.id.accountAuth, false)
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                openFragment(FragmentSearch())
            }
            R.id.NewsSources -> openFragment(FragmentNewsFromSources())
            R.id.menuNews -> fragmentCheckOnlineMode()
            R.id.menuNotes -> openFragment(FragmentNotes())
            R.id.menuBookmarks -> openFragment(FragmentBookmarks())
            R.id.menuSettings -> openFragment(FragmentSettings())
            R.id.categoryGlobal -> checkOfflineMode(Page.GLOBAL.index)
            R.id.categoryBusiness -> checkOfflineMode(Page.BUSINESS.index)
            R.id.categoryHealth -> checkOfflineMode(Page.HEALTH.index)
            R.id.categoryMyNews -> checkOfflineMode(Page.MY_NEWS.index)
            R.id.categoryEntertainment -> checkOfflineMode(Page.ENTERTAINMENT.index)
            R.id.categoryScience -> checkOfflineMode(Page.SCIENCE.index)
            R.id.categorySport -> checkOfflineMode(Page.SPORTS.index)
            R.id.categoryTechnologies -> checkOfflineMode(Page.TECHNOLOGY.index)
            R.id.aboutApp -> openFragment(FragmentAboutApp())
            R.id.messageProgrammer -> openFragment(FragmentMessageToProgrammer())
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkOfflineMode(position: Int) {
        if (!sharedPreferences.getBoolean(OFFLINE_MODE, false)) fragmentMain.navigationViewPager(
            position
        )
        else openFragment(FragmentOffline())
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == DARK_MODE) {
            if (sharedPreferences!!.getBoolean(DARK_MODE, false))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, R.string.onGoodGoogleAuth, Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(
                    this, R.string.onErorGoogleAuth,
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun fragmentCheckOnlineMode() {
        if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
            openFragment(FragmentOffline())
        else {
            openFragment(fragmentMain)
        }
    }
}