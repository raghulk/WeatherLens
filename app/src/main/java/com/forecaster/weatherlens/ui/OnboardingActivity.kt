package com.forecaster.weatherlens.ui

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.forecaster.weatherlens.MainActivity
import com.forecaster.weatherlens.R
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest


class OnBoardingActivity : AppIntro() {

    private lateinit var mainActivity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            AppIntroFragment.newInstance(
                title = "Welcome to Weather Lens",
                description = "You can track the weather for your location in no time",
                imageDrawable = R.drawable.weather
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = "Permission requirements first...",
                description = "We would need access to your device's location to get provide accurate weather information",
                imageDrawable = R.drawable.ic_location
            )
        )
        askForPermissions(
            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            slideNumber = 2,
            required = true
        )
        enableLoc()
        addSlide(
            AppIntroFragment.newInstance(
                title = "Swipe to the future!",
                description = "You can swipe the card left or right to view weather information for the next day!",
                imageDrawable = R.drawable.card_swipe
            )
        )
    }

    private fun enableLoc() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(6 * 1000)
            .setFastestInterval(4 * 1000)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val pendingResult = LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(builder.build())

        pendingResult.addOnSuccessListener { response ->
            val states = response.locationSettingsStates
            if (states.isLocationPresent) {

            }
        }
        pendingResult.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // Handle result in onActivityResult()
                    e.startResolutionForResult(this, 999)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            999 -> {
                if (resultCode == 0) {
                    Toast.makeText(
                        this,
                        "Please turn on location to get weather for your location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
