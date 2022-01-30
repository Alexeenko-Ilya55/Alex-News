package com.myproject.alexnews.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myproject.alexnews.databinding.FragmentMessegetoProgrammerBinding

class FragmentMessageToProgrammer : Fragment() {

    lateinit var binding: FragmentMessegetoProgrammerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessegetoProgrammerBinding.inflate(inflater, container, false)
        activity!!.setTitle("Связь")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            btnSent.setOnClickListener {
                val email = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:alexeenko.ilya55@gmail.com"));
                email.putExtra(Intent.EXTRA_SUBJECT, subject.text.toString());
                email.putExtra(Intent.EXTRA_TEXT, message.text.toString());
                startActivity(email)
            }
        }
    }
}