package com.code_crusaders.floodsafe.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.code_crusaders.floodsafe.models.FloodedArea
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation

@Composable
fun WaterLoggingAnnotation(floodedArea: FloodedArea) {
    val context = LocalContext.current

    CircleAnnotation(
        point = Point.fromLngLat(
            floodedArea.location.longitude,
            floodedArea.location.latitude
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
            Color(
                red = 3,
                green = 169,
                blue = 244,
                alpha = 255
            ).copy(alpha = 0.4f)

    }
}