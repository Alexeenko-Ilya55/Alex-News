package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.Settings
import com.myproject.alexnews.databinding.FragmentChangeMyNewsBinding


class FragmentChangeMyNews : Fragment() {

    lateinit var binding: FragmentChangeMyNewsBinding
    private lateinit var checkBoxBusiness: CheckBox
    private lateinit var checkBoxTechnologies: CheckBox
    private lateinit var checkBoxSports: CheckBox
    private lateinit var checkBoxGlobal: CheckBox
    private lateinit var checkBoxHealth: CheckBox
    private lateinit var checkBoxScience: CheckBox
    private lateinit var checkBoxEntertainment: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.setTitle(R.string.change)
        binding = FragmentChangeMyNewsBinding.inflate(inflater, container, false)
        binding.apply {
            checkBoxBusiness = ChangeMyNewsCheckBoxBusiness
            checkBoxSports = ChangeMyNewsCheckBoxSports
            checkBoxTechnologies = ChangeMyNewsCheckBoxTechnologies
            checkBoxGlobal = ChangeMyNewsCheckBoxGlobal
            checkBoxHealth = ChangeMyNewsCheckBoxHealth
            checkBoxScience = ChangeMyNewsCheckBoxScience
            checkBoxEntertainment = ChangeMyNewsCheckBoxEntertainment

            onStartIsChecked()
            AddAllCategories.setOnClickListener {
                onClick()
            }
            // Inflate the layout for this fragment
            return binding.root
        }
    }

    override fun onDestroy() {

        Settings.categoryBusiness = checkBoxBusiness.isChecked
        Settings.categoryTechnologies = checkBoxTechnologies.isChecked
        Settings.categorySports = checkBoxSports.isChecked
        Settings.categoryGlobal = checkBoxGlobal.isChecked
        Settings.categoryHealth = checkBoxHealth.isChecked
        Settings.categoryScience = checkBoxScience.isChecked
        Settings.categoryEntertainment = checkBoxEntertainment.isChecked
        super.onDestroy()
    }

    private fun onStartIsChecked() {
        if (Settings.categoryBusiness) checkBoxBusiness.toggle()
        if (Settings.categorySports) checkBoxSports.toggle()
        if (Settings.categoryScience) checkBoxScience.toggle()
        if (Settings.categoryEntertainment) checkBoxEntertainment.toggle()
        if (Settings.categoryHealth) checkBoxHealth.toggle()
        if (Settings.categoryGlobal) checkBoxGlobal.toggle()
        if (Settings.categoryTechnologies) checkBoxTechnologies.toggle()
    }

    private fun onClick() {
        if (checkBoxEntertainment.isChecked && checkBoxGlobal.isChecked &&
            checkBoxHealth.isChecked && checkBoxTechnologies.isChecked &&
            checkBoxBusiness.isChecked && checkBoxSports.isChecked
            && checkBoxScience.isChecked
        ) {
            checkBoxSports.toggle()
            checkBoxBusiness.toggle()
            checkBoxTechnologies.toggle()
            checkBoxHealth.toggle()
            checkBoxEntertainment.toggle()
            checkBoxScience.toggle()
            checkBoxGlobal.toggle()
        } else {
            if (!checkBoxBusiness.isChecked) checkBoxBusiness.toggle()
            if (!checkBoxSports.isChecked) checkBoxSports.toggle()
            if (!checkBoxScience.isChecked) checkBoxScience.toggle()
            if (!checkBoxTechnologies.isChecked) checkBoxTechnologies.toggle()
            if (!checkBoxHealth.isChecked) checkBoxHealth.toggle()
            if (!checkBoxGlobal.isChecked) checkBoxGlobal.toggle()
            if (!checkBoxEntertainment.isChecked) checkBoxEntertainment.toggle()
        }
    }
}