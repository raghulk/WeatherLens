package com.forecaster.weatherlens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.forecaster.weatherlens.databinding.ActivityMainBinding
import com.forecaster.weatherlens.ui.OnBoardingActivity
import com.forecaster.weatherlens.ui.lens.LensFragment
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIfIntroCompleted()

        initializeAndFetchRemoteConfig()
    }

    private fun checkIfIntroCompleted() {
        sharedPref = getPreferences(Context.MODE_PRIVATE)
        sharedPref.let {
            if (!sharedPref.contains(getString(R.string.onBoarding_done))) {
                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)
                with(sharedPref.edit()) {
                    this.putBoolean(getString(R.string.onBoarding_done), true)
                    this.apply()
                }
            }
            else getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            if (it != null) {
                LensFragment.location = it
                with(sharedPref.edit()) {
                    this.putString(getString(R.string.latitude), it.latitude.toString())
                    this.putString(getString(R.string.longitude), it.longitude.toString())
                    this.apply()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please turn on location to get weather for your location",
                    Toast.LENGTH_SHORT
                ).show()
                getLocationFromSharedPref()
            }
            drawUI()
        }.addOnFailureListener {
            getLocationFromSharedPref()
            drawUI()
        }
    }

    private fun getLocationFromSharedPref() {
        sharedPref.let {
            LensFragment.location.latitude =
                it.getString(getString(R.string.latitude), "0.0")?.toDouble() ?: 0.0
            LensFragment.location.longitude =
                it.getString(getString(R.string.longitude), "0.0")?.toDouble() ?: 0.0
        }

    }

    private fun initializeAndFetchRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(
                        "MainAct",
                        "Config params updated: $updated ${R.xml.remote_config_defaults}"
                    )
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun drawUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }
}