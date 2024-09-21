package com.code_crusaders.floodsafe.models

import com.google.firebase.firestore.GeoPoint

/**
 * Created by Leons Chelladurai on 21September2024
 * Copyright © 2023 Parllay. a division of Parllay Inc. All Rights Reserved
 */
data class DisasterManagementService(
    val location: GeoPoint,
    val type: String,
    val contactInfo: String
)
