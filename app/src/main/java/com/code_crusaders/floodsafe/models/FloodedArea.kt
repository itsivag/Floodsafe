package com.code_crusaders.floodsafe.models

import com.google.firebase.firestore.GeoPoint

data class FloodedArea(
    val location: GeoPoint,
    val depth: Int,
    val radius: Int
)
