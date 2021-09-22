package com.forecaster.weatherlens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.forecaster.weatherlens.databinding.ActivityMainBinding
import com.forecaster.weatherlens.ui.lens.LensFragment
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()

        getLocation()
        initializeAndFetchRemoteConfig()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        sharedPref?.let {
            LensFragment.location.latitude =
                it.getString(getString(R.string.latitude), "0.0")?.toDouble() ?: 0.0
            LensFragment.location.longitude =
                it.getString(getString(R.string.longitude), "0.0")?.toDouble() ?: 0.0
        }

        getFusedLocationProviderClient(this@MainActivity).lastLocation.addOnSuccessListener {
            if(it!=null) {
                LensFragment.location = it
                with(sharedPref?.edit()){
                    this?.putString(getString(R.string.latitude), it.latitude.toString())
                    this?.putString(getString(R.string.longitude), it.longitude.toString())
                    this?.apply()
                }
            } else Toast.makeText(this, "Please turn on location to get weather for your location", Toast.LENGTH_SHORT).show()
            drawUI()
        }.addOnFailureListener {
            Toast.makeText(this, "Please turn on location to get weather for your location", Toast.LENGTH_SHORT).show()
            drawUI()
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
                    Log.d("MainAct", "Config params updated: $updated ${R.xml.remote_config_defaults}")
                }
            }
    }

    private fun drawUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),123)
            return
        }
    }
}