package com.code_crusaders.floodsafe

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FmdBad
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.code_crusaders.floodsafe.ui.theme.FloodsafeTheme
import com.google.android.gms.location.*
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import  com.code_crusaders.floodsafe.data.DataHandler
import com.code_crusaders.floodsafe.models.FloodedArea
import com.code_crusaders.floodsafe.presentation.WaterLoggingAnnotation

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

    private val localDataStore = DataHandler().getLocalSyntheticData()

    // Access data from the LocalDataStore object
    private val floodedAreas = localDataStore.floodedAreas
    val disasterManagementServices = localDataStore.disasterManagementServices
    val emergencySupplyDrops = localDataStore.emergencySupplyDrops

    init {
        fetchFloodData()
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
        checkLocationPermission()

//        sampleDataPrint()
    }

    private fun fetchFloodData() {
        floodedAreas.forEach { floodedArea ->
            Log.d(
                "MainActivity",
                "Flooded Area: ${floodedArea.location}, Depth: ${floodedArea.depth}, Radius: ${floodedArea.radius}"
            )
        }
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

    @SuppressLint("MissingPermission")
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
                val mapViewportState = rememberMapViewportState {
                    // Set the initial camera position
                    setCameraOptions {
                        center(Point.fromLngLat(0.0, 0.0))
                        zoom(0.0)
                        pitch(0.0)
                    }
                }
                val userLocation by remember { mutableStateOf(location) }
                val context = LocalContext.current

                Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            mapViewportState.flyTo(
                                cameraOptions = cameraOptions {
                                    center(
                                        Point.fromLngLat(
                                            userLocation.longitude,
                                            userLocation.latitude
                                        )
                                    )
                                    zoom(14.0)
                                    pitch(45.0)
                                },
                                MapAnimationOptions.mapAnimationOptions { duration(5000) }
                            )
                        }) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Current Location"
                        )
                    }
                },
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                IconButton(onClick = {

                                }) {
                                    Icon(
                                        Icons.Filled.FmdBad,
                                        contentDescription = "Localized description"
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Call,
                                        contentDescription = "Localized description",
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        MapboxMap(
                            Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            mapViewportState = mapViewportState,
                            style = {
                                MapStyle(style = Style.SATELLITE_STREETS)
                            }
                        ) {
//                            MapEffect(key1 = true) { mapView ->
//
//                            }
                            // Add a single circle annotation at null island.

                            for (i in floodedAreas) {
                                WaterLoggingAnnotation(i)
                            }
                        }
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
