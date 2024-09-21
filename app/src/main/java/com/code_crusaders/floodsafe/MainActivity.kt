package com.code_crusaders.floodsafe

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.code_crusaders.floodsafe.ui.theme.FloodsafeTheme
import com.google.android.gms.location.*
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getLastLocationOrStartLocationUpdates()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getLastLocationOrStartLocationUpdates()
            }

            else -> {
                // No location access granted.
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapboxOptions.accessToken =
            "pk.eyJ1IjoiaXRzaXZhZyIsImEiOiJjbGd5eWIyODIwZWI5M3Bwbm1rZjN0azM1In0.kCxW9Q7SksXt_BwV5QT5MQ"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateLocationUI(location)
                }
            }
        }

        enableEdgeToEdge()
        setContent {
//            FloodsafeTheme {
//                var userLocation by remember { mutableStateOf<Location?>(null) }
//
//                Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
//                    FloatingActionButton(
//                        onClick = { /*TODO*/ }) {
//                        IconButton(onClick = { /*TODO*/ }) {
//                            Icon(
//                                imageVector = Icons.Default.LocationOn,
//                                contentDescription = "Current Location"
//                            )
//                        }
//                    }
//                }) { innerPadding ->
//                    Column(
//                        Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize()
//                    ) {
//                        MapboxMap(
//                            Modifier
//                                .weight(1f)
//                                .fillMaxSize(),
//                            mapViewportState = rememberMapViewportState {
//                                setCameraOptions {
//                                    zoom(2.0)
//                                    center(Point.fromLngLat(-98.0, 39.5))
//                                    pitch(0.0)
//                                    bearing(0.0)
//                                }
//                            },
//                        )
////                        LocationDisplay(userLocation)
////                        Button(onClick = { checkLocationPermission() }) {
////                            Text("Update Location")
////                        }
//                    }
//                }
//            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocationOrStartLocationUpdates()
            }

            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getLastLocationOrStartLocationUpdates() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    updateLocationUI(location)
                } else {
                    startLocationUpdates()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                startLocationUpdates()
            }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun updateLocationUI(location: Location) {
        setContent {
            FloodsafeTheme {
                var userLocation by remember { mutableStateOf(location) }

                Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Current Location"
                        )
                    }
                }) { innerPadding ->
                    Column(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        MapboxMap(
                            Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            mapViewportState = rememberMapViewportState {
                                setCameraOptions {
                                    zoom(10.0)
                                    center(Point.fromLngLat(location.longitude, location.latitude))
                                    pitch(0.0)
                                    bearing(0.0)
                                }
                            },
                        )
                        LocationDisplay(userLocation)
//                        Button(onClick = { checkLocationPermission() }) {
//                            Text("Update Location")
//                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

@Composable
fun LocationDisplay(location: Location?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Current Location:")
        Text(text = "Latitude: ${location?.latitude ?: "Unknown"}")
        Text(text = "Longitude: ${location?.longitude ?: "Unknown"}")
    }
}