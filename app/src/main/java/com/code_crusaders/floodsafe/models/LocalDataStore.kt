package com.code_crusaders.floodsafe.models

import com.google.firebase.firestore.GeoPoint

/**
 * Created by Leons Chelladurai on 21September2024
 * Copyright © 2023 Parllay. a division of Parllay Inc. All Rights Reserved
 */
data class LocalDataStore(
    val floodedAreas: List<FloodedArea>,
    val disasterManagementServices: List<DisasterManagementService>,
    val emergencySupplyDrops: List<EmergencySupplyDrop>
)
