package com.myproject.alexnews.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Build.ID
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.*

import com.myproject.alexnews.databinding.ActivityMainBinding
import com.myproject.alexnews.fragments.*
import com.myproject.alexnews.model.Article
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mToggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var fragmentMain: FragmentMain
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nawViewInit()
        sharedPreferences()
        actionBarInit()
        signInAccountInit()
        initFirebase()

        super.onCreate(savedInstanceState)

        fragmentMain = FragmentMain()
        fragmentMain.arguments = Bundle().apply {
            putInt("Pos", 0)
        }
        openFragment(fragmentMain)
    }

    private fun signInAccountInit(){
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
                Toast.makeText(this,"Error, try again",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun nawViewInit() {
        binding.navView.bringToFront()
        binding.navView.isVerticalScrollBarEnabled
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun actionBarInit() {
        mToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
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
        if (mToggle.onOptionsItemSelected(item))
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
        if(auth.currentUser != null){
            auth.signOut()
            item.title = getString(R.string.Auth)
            Toast.makeText(this,R.string.youLogOut,Toast.LENGTH_SHORT).show()
        }
        else{
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
            R.id.menuNews -> openFragment(fragmentMain)
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
                Toast.makeText(this,R.string.onGoodGoogleAuth,Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this,R.string.onErorGoogleAuth,
                    Toast.LENGTH_LONG).show()
        }
    }
}