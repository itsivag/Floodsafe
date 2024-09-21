package com.code_crusaders.floodsafe.models

import com.google.firebase.firestore.GeoPoint

/**
 * Created by Leons Chelladurai on 21September2024
 * Copyright © 2023 Parllay. a division of Parllay Inc. All Rights Reserved
 */
data class EmergencySupplyDrop(
    val location: GeoPoint,
    val status: String,
    val notification: Notification?
)

data class Notification(
    val title: String,
    val body: String,
    val sent: Boolean
)
