package com.code_crusaders.floodsafe

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.code_crusaders.floodsafe.ui.theme.FloodsafeTheme
import com.google.android.gms.location.*
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions

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
                            mapViewportState = mapViewportState,
                            style = {
                                MapStyle(style = Style.SATELLITE_STREETS)
                            }
                        ) {
//                            MapEffect(key1 = true) { mapView ->
//
//                            }
                            // Add a single circle annotation at null island.
                            CircleAnnotation(
                                point = Point.fromLngLat(
                                    userLocation.longitude,
                                    userLocation.latitude
                                ),
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Clicked on Circle Annotation: $it",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    true
                                }
                            ) {
                                circleStrokeWidth = 2.0
                                circleStrokeColor = Color(
                                    red = 3,
                                    green = 169,
                                    blue = 244,
                                    alpha = 255
                                )
                                circleRadius = 40.0
                                circleColor =
                                    Color(red = 3, green = 169, blue = 244, alpha = 255).copy(alpha = 0.4f)

                            }

                            PointAnnotationGroup(
                                annotations = points.map {
                                    PointAnnotationOptions()
                                        .withPoint(it)
                                        .withIconImage(ICON_FIRE_STATION)
                                },
                                annotationConfig = AnnotationConfig(
                                    annotationSourceOptions = AnnotationSourceOptions(
                                        clusterOptions = ClusterOptions(
                                            textColorExpression = Expression.color(Color.Yellow),
                                            textColor = Color.BLACK,
                                            textSize = 20.0,
                                            circleRadiusExpression = literal(25.0),
                                            colorLevels = listOf(
                                                Pair(100, Color.RED),
                                                Pair(50, Color.BLUE),
                                                Pair(0, Color.GREEN)
                                            )
                                        )
                                    )
                                ),
                                onClick = {
                                    Toast.makeText(
                                        this@PointAnnotationClusterActivity,
                                        "Clicked on Point Annotation Cluster: $it",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    true
                                }
                            )
                        }
                    }
                        }
                    }
//                        LocationDisplay(userLocation)
//                        Button(onClick = { checkLocationPermission() }) {
//                            Text("Update Location")
//                        }
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