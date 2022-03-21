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
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.NO_PASSWORD
import com.myproject.alexnews.`object`.PASSWORD_NOTES
import com.myproject.alexnews.databinding.FragmentContentNewsBinding
import com.myproject.alexnews.viewModels.FragmentContentNewsViewModel
import com.myproject.repository.model.Article
import java.util.concurrent.Executor

class FragmentContentNews(private val news: Article) : Fragment() {

    lateinit var binding: FragmentContentNewsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val newsUrl = news.url
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private lateinit var viewModel: FragmentContentNewsViewModel

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentNewsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[FragmentContentNewsViewModel::class.java]
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        requireActivity().setTitle(R.string.News)
        setHasOptionsMenu(true)
        auth = Firebase.auth
        auth.currentUser

        binding.apply {
            WebView.webViewClient = WebViewClient()
            WebView.apply {
                WebView.loadUrl(newsUrl)
                settings.safeBrowsingEnabled = true
                settings.javaScriptEnabled = true
                WebView.doOnAttach { }
            }

            floatingActionButton.setOnClickListener {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT, newsUrl)
                    this.type = "text/plain"
                }
                startActivity(shareIntent)
            }
            binding.NotesButton.setOnClickListener {
                if (sharedPreferences.getBoolean(NO_PASSWORD, false) && news.notes != "") {
                    authTouchId()
                } else
                    showEditTextNotes()
            }
        }
        return binding.root
    }

    @SuppressLint("InflateParams")
    private fun showEditTextNotes() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_notes_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextNotes)
        editText.setText(news.notes)
        with(builder) {
            setTitle(R.string.menu_Notes)
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                if (!(editText.text.toString() == "" && editText.text == null)) {
                    news.notes = editText.text.toString()
                    if (!news.bookmarkEnable)
                        news.bookmarkEnable = true

                    viewModel.updateElement(requireContext(), news)
                }
            }
            setNegativeButton(getString(R.string.goBack)) { _, _ ->
                Log.i("MyLog", "GoBack pressed")
            }
            setView(dialogLayout)
            show()
        }
    }

    @SuppressLint("InflateParams")
    private fun showEditTextPassword() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_password_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.myPassword)
        with(builder) {
            setTitle(getString(R.string.InputPassword))
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                if (editText.text.toString() == sharedPreferences.getString(PASSWORD_NOTES, ""))
                    showEditTextNotes()
                else
                    showEditTextPassword()
            }
            setNegativeButton(getString(R.string.goBack)) { _, _ ->
                Log.e("MyLog", "CloseEditText")
            }
            setView(dialogLayout)
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_content_news_menu, menu)
        menu.setGroupVisible(R.id.group_bookmsark, false)
        if (news.bookmarkEnable) {
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
        news.bookmarkEnable = !news.bookmarkEnable
        if (news.bookmarkEnable) {
            item.setIcon(R.drawable.bookmark_enable_icon_item)
            viewModel.updateElement(requireContext(), news)
        } else {
            item.setIcon(R.drawable.bookmark_action_bar_content)
            viewModel.updateElement(requireContext(), news)
        }
    }

    private fun authTouchId() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = androidx.biometric.BiometricPrompt(
            this,
            executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showEditTextPassword()
                }

                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showEditTextNotes()
                }
            })
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.Biometric_auth))
            .setNegativeButtonText(getString(R.string.InputYourPassword))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }


}