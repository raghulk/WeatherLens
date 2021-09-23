package com.forecaster.weatherlens.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.forecaster.weatherlens.R
import com.forecaster.weatherlens.databinding.FragmentSettingsBinding
import android.content.Intent
import android.net.Uri

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        binding.apply {
            lifecycleOwner = this@SettingsFragment
            sharedPref?.let {
                toggleButton.isChecked = it.getBoolean(getString(R.string.metric), false)
            }
            toggleButton.setOnClickListener{
                with(sharedPref?.edit()) {
                    if (toggleButton.isChecked) this?.putBoolean(getString(R.string.metric), true)
                    else this?.putBoolean(getString(R.string.metric), false)
                    this?.apply()
                }
            }
        }

        setupButtons()
        return root
    }

    private fun setupButtons() {
        setupAboutDeveloper()
        setupContributeProject()
    }

    private fun setupAboutDeveloper() {
        val aboutView: TextView = binding.aboutDeveloper
        aboutView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linkedin.com/in/raghul-krishnan"))
            startActivity(browserIntent)
        }
    }

    private fun setupContributeProject() {
        val contributions: TextView = binding.contributeProject
        contributions.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/raghulk/WeatherLens")
            )
            startActivity(browserIntent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}