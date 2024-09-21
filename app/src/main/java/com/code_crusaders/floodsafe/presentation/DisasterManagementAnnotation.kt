package com.code_crusaders.floodsafe.presentation

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.code_crusaders.floodsafe.models.DisasterManagementService
import com.mapbox.geojson.Point
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

@Composable
fun DisasterManagementAnnotation(
    disasterManagement: DisasterManagementService,
    isDisasterManagementPopUpVisible: () -> Unit
) {
    val context = LocalContext.current
    val isDisasterPopUpVisible by remember {
        mutableStateOf(false)
    }
// Add a ViewAnnotation to the map
    ViewAnnotation(
        options = viewAnnotationOptions {
            // set the view annotation associated geometry
            geometry(
                Point.fromLngLat(
                    disasterManagement.location.longitude,
                    disasterManagement.location.latitude
                )
            )
            annotationAnchor {
                anchor(ViewAnnotationAnchor.BOTTOM)
            }
            allowOverlap(false)
        }
    ) {
        // You can add the content to be drawn in the ViewAnnotation using Composable functions, e.g. to insert a button:
        IconButton(
            onClick = {
                Toast.makeText(context, disasterManagement.contactInfo, LENGTH_SHORT).show()
            }
        ) {
            when (disasterManagement.type) {
                "Fire Department" -> {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = disasterManagement.type,
                        tint = Color.Yellow
                    )
                }

                "Police Department" -> {
                    Icon(
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = disasterManagement.type,
                        tint = Color.Blue
                    )

                }

                "Medical Services" -> {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = disasterManagement.type,
                        tint = Color.Red
                    )
                }

                else -> {}
            }

        }
    }
}

//    CircleAnnotation(
//        point = Point.fromLngLat(
//            floodedArea.location.longitude,
//            floodedArea.location.latitude
//        ),
//        onClick = {
////            changeBottomSheetState()
//            true
//        }
//    ) {
//        circleStrokeWidth = 2.0
//        circleStrokeColor = Color(
//            red = 3,
//            green = 169,
//            blue = 244,
//            alpha = 255
//        )
//        circleRadius = (floodedArea.radius / 5).toDouble()
//        circleColor =
//            Color(
//                red = 3,
//                green = 169,
//                blue = 244,
//                alpha = 255
//            ).copy(alpha = 0.4f)
//    }
