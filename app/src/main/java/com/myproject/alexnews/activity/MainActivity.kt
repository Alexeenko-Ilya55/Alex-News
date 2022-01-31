package com.myproject.alexnews.activity

import android.app.AlarmManager
import android.app.PendingIntent
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
import com.myproject.alexnews.service.NotificationReceiver
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mToggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var fragmentMain: FragmentMain
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = PreferenceManager.getDefaultSharedPreferences(this)
        nawViewInit()
        sharedPreferences()
        actionBarInit()
        signInAccountInit()
        initFirebase()
        initNotification()

        fragmentMain = FragmentMain()
        fragmentMain.arguments = Bundle().apply {
            putInt("Pos", 0)
        }
        if(savedInstanceState == null) {
            fragmentCheckOnlineMode()
        }
    }

    private fun initNotification() {
        val calendar = Calendar.getInstance()
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,AlarmManager.INTERVAL_DAY,pendingIntent)
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
            R.id.menuNews -> fragmentCheckOnlineMode()
            R.id.menuNotes -> openFragment(FragmentNotes())
            R.id.menuBookmarks -> openFragment(FragmentBookmarks())
            R.id.menuSettings -> openFragment(FragmentSettings())
            R.id.categoryGlobal -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(4)
                                    else openFragment(FragmentOffline())
            R.id.categoryBusiness -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(3)
                                        else openFragment(FragmentOffline())
            R.id.categoryHealth -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(5)
                                    else openFragment(FragmentOffline())
            R.id.categoryMyNews -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(0)
                                    else openFragment(FragmentOffline())
            R.id.categoryEntertainment -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(7)
                                            else openFragment(FragmentOffline())
            R.id.categoryScience -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(6)
                                        else openFragment(FragmentOffline())
            R.id.categorySport -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(2)
                                    else openFragment(FragmentOffline())
            R.id.categoryTechnologies -> if(!sp.getBoolean("OfflineMode", false)) fragmentMain.navVP(1)
                                    else openFragment(FragmentOffline())
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
    fun fragmentCheckOnlineMode(){
        if (sp.getBoolean("OfflineMode", false))
            openFragment(FragmentOffline())
        else {
            openFragment(fragmentMain)
        }
    }
}