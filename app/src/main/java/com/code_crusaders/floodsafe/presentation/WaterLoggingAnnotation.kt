package com.code_crusaders.floodsafe.presentation

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.code_crusaders.floodsafe.models.FloodedArea
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation

@Composable
fun WaterLoggingAnnotation(
    floodedArea: FloodedArea,
    changeBottomSheetState: () -> Unit
) {
    val context = LocalContext.current

    CircleAnnotation(
        point = Point.fromLngLat(
            floodedArea.location.longitude,
            floodedArea.location.latitude
        ),
        onClick = {
            Toast.makeText(context, "Flood Level : " + floodedArea.depth + " cm", LENGTH_SHORT)
                .show()
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
        circleRadius = (floodedArea.radius / 5).toDouble()
        circleColor =
            Color(
                red = 3,
                green = 169,
                blue = 244,
                alpha = 255
            ).copy(alpha = 0.4f)
    }
}


