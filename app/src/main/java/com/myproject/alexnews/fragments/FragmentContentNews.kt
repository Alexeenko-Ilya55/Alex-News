package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.dao.FirebaseDB
import com.myproject.alexnews.databinding.FragmentContentNewsBinding
import com.myproject.alexnews.model.Article
import java.util.concurrent.Executor


class FragmentContentNews(val data: Article) : Fragment() {

    lateinit var binding: FragmentContentNewsBinding

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseDB()
    private lateinit var ps: SharedPreferences
    private val myUrl = data.url

    lateinit var executor: Executor
    lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentNewsBinding.inflate(inflater, container, false)

        ps = PreferenceManager.getDefaultSharedPreferences(context!!)

        activity!!.setTitle(R.string.News)
        setHasOptionsMenu(true)
        auth = Firebase.auth
        auth.currentUser


        binding.apply {
            WebView.webViewClient = WebViewClient()
            WebView.apply {
                WebView.loadUrl(myUrl)
                settings.safeBrowsingEnabled = true
                settings.javaScriptEnabled = true
                WebView.doOnAttach { }
            }

            floatingActionButton.setOnClickListener {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT, myUrl)
                    this.type = "text/plain"
                }
                startActivity(shareIntent)
            }
            binding.NotesButton.setOnClickListener {

                if (ps.getBoolean("notPsw", false) && data.notes !="") {
                    authTouchId()
                }
                else
                    showEditTextNotes()
            }
        }
        return binding.root
    }

    private fun showEditTextNotes() {
            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_notes_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editTextNotes)
            editText.setText(data.notes)
            with(builder) {
                setTitle(R.string.menu_Notes)
                setPositiveButton("ОК") { dialog, which ->
                    if (!(editText.text.toString() == "" && editText.text == null)) {
                        data.notes = editText.text.toString()
                        if (!data.bookmarkEnable) data.bookmarkEnable = true
                        db.deleteFromFB(data.url)
                        db.addToFirebase(data)
                    }
                }
                setNegativeButton("Назад") { dialog, which ->
                    Log.e("MyLog", "CloseEditText")
                }
                setView(dialogLayout)
                show()
            }
    }
    private fun showEditTextPassword() {
            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_password_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.myPassword)
            with(builder) {
                setTitle("Введите пароль")
                setPositiveButton("ОК") { dialog, which ->
                        if (editText.text.toString() == ps.getString("PSW_Notes", ""))
                            showEditTextNotes()
                        else
                            showEditTextPassword()
                }
                setNegativeButton("Назад") { dialog, which ->
                        Log.e("MyLog", "CloseEditText")
                }
                setView(dialogLayout)
                show()
            }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_content_news_menu, menu)
        menu.setGroupVisible(R.id.group_bookmsark, false)
        if (data.bookmarkEnable) {
            menu.setGroupVisible(R.id.Enable, true)
            menu.setGroupVisible(R.id.notEnable, false)
        } else {
            menu.setGroupVisible(R.id.notEnable, true)
            menu.setGroupVisible(R.id.Enable, false)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuBookmarks_content)
            clickOnBookmark(item)
        if (item.itemId == R.id.menuBookmarks_content_enable)
            clickOnBookmark(item)
        return true
    }

    private fun clickOnBookmark(item: MenuItem) {
        data.bookmarkEnable = !data.bookmarkEnable
        if (data.bookmarkEnable) {
            item.setIcon(R.drawable.bookmark_enable_icon_item)
            db.addToFirebase(data)
        } else {
            item.setIcon(R.drawable.bookmark_action_bar_content)
            db.deleteFromFB(data.url)
        }
    }

    private fun authTouchId(){
        executor = ContextCompat.getMainExecutor(context!!)
        biometricPrompt = androidx.biometric.BiometricPrompt(this,executor,object :androidx.biometric.BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showEditTextPassword()
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("MyLog","failed")
            }
            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                showEditTextNotes()
            }
        })
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Биометрическая аунтефикация")
            .setNegativeButtonText("Ввести пароль")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }









}