package com.example.weatherappjc.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import com.example.weatherappjc.activity.viewmodel.WeatherViewModel
import com.example.weatherappjc.screens.CheckNetworkConnectionScreen
import com.example.weatherappjc.screens.DraggableCard
import com.example.weatherappjc.ui.theme.WeatherAppJCTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
    class MainActivity : ComponentActivity() {
        private val REQUEST_LOCATION = 1
        private lateinit var fusedLocationClient: FusedLocationProviderClient

        private val viewModel by viewModels<WeatherViewModel>()
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            setContent {

                val location = remember { mutableStateOf("London") } // default value
                WeatherAppJCTheme {
                    CheckNetworkConnectionScreen(viewModel, location)
                    DraggableCard()
                }
            }

            getLastKnownLocation()

        }

        private fun getLastKnownLocation() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "The location is not granted, Weather app needs location for weather show",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.getWeather("London")
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Toast.makeText(this, "Location needed", Toast.LENGTH_SHORT).show()
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION
                    )
                }
            } else {
                // Permission has already been granted
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.getWeather("${location.latitude},${location.longitude}")
                    }
                }
            }
        }

}
