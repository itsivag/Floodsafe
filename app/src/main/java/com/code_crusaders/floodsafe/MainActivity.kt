package com.code_crusaders.floodsafe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.code_crusaders.floodsafe.models.DisasterManagementService
import com.code_crusaders.floodsafe.models.EmergencySupplyDrop
import com.code_crusaders.floodsafe.models.FloodedArea
import com.code_crusaders.floodsafe.ui.theme.FloodsafeTheme

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FirebaseFirestore


//pk.eyJ1IjoiaXRzaXZhZyIsImEiOiJjbGd5eWIyODIwZWI5M3Bwbm1rZjN0azM1In0.kCxW9Q7SksXt_BwV5QT5MQ -> PUBLIC
//sk.eyJ1IjoiaXRzaXZhZyIsImEiOiJjbTFic2xkMmkxdGI2MmpxeDAwZ2F3a3JxIn0.TcwG1TV1gXkW3iA1PIAJmQ -> PRIVATE

class MainActivity : ComponentActivity() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FloodsafeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        populateData()
    }

    private fun populateData() {
        //createSyntheticData(db)
        retrieveData()
    }

    fun retrieveData() {
        // Retrieve flooded areas
        db.collection("cities")
            .document("chennai")
            .collection("floodedAreas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val floodedArea = document.toObject(FloodedArea::class.java)
                    // Process flooded area data
                    Log.d("Firestore","Flooded Area: ${floodedArea.location}, Depth: ${floodedArea.depth}, Radius: ${floodedArea.radius}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to retrieve flooded areas", exception)
            }

        // Retrieve disaster management services
        db.collection("cities")
            .document("chennai")
            .collection("disasterManagementServices")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val disasterManagementService = document.toObject(DisasterManagementService::class.java)
                    // Process disaster management service data
                    Log.d("Firestore","Disaster Management Service: ${disasterManagementService.location}, Type: ${disasterManagementService.type}, Contact Info: ${disasterManagementService.contactInfo}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to retrieve disaster management services", exception)
            }

        // Retrieve emergency supply drops
        db.collection("cities")
            .document("chennai")
            .collection("emergencySupplyDrops")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val emergencySupplyDrop = document.toObject(EmergencySupplyDrop::class.java)
                    // Process emergency supply drop data
                    Log.d("Firestore","Emergency Supply Drop: ${emergencySupplyDrop.location}, Status: ${emergencySupplyDrop.status}, Notification: ${emergencySupplyDrop.notification}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to retrieve emergency supply drops", exception)
            }
    }


    fun createSyntheticData(db: FirebaseFirestore) {
        // Define the bounding coordinates
        val guindy = GeoPoint(13.006501220285864, 80.20267526077417)
        val adayar = GeoPoint(13.006183501265852, 80.25990311603822)
        val keelkatalai = GeoPoint(12.952439073440658, 80.18676837982197)
        val neelankarai = GeoPoint(12.94626024306985, 80.25878197134104)

        // Generate random points within the bounding coordinates
        fun generateRandomPoint(): GeoPoint {
            val latitude = (guindy.latitude + keelkatalai.latitude) / 2 + (Math.random() - 0.5) * (guindy.latitude - keelkatalai.latitude)
            val longitude = (guindy.longitude + adayar.longitude) / 2 + (Math.random() - 0.5) * (guindy.longitude - adayar.longitude)
            return GeoPoint(latitude, longitude)
        }

        // Generate synthetic data
        val floodedAreas = (1..10).map {
            hashMapOf(
                "location" to generateRandomPoint(),
                "depth" to (5..10).random(),
                "radius" to (5..100).random()
            )
        }

        val disasterManagementServices = (1..10).map {
            hashMapOf(
                "location" to generateRandomPoint(),
                "type" to listOf("Fire Department", "Police Department", "Medical Services").random(),
                "contactInfo" to "Contact: 1234567890, Email: example@example.com" // Replace with random values
            )
        }

        val emergencySupplyDrops = listOf(
            hashMapOf(
                "location" to generateRandomPoint(),
                "status" to "upcoming",
                "notification" to hashMapOf(
                    "title" to "Emergency Supply Drop",
                    "body" to "A supply drop will be happening at this location.",
                    "sent" to false
                )
            ),
            hashMapOf(
                "location" to generateRandomPoint(),
                "status" to "ongoing",
                "notification" to hashMapOf(
                    "title" to "Emergency Supply Drop in Progress",
                    "body" to "Supplies are being distributed at this location.",
                    "sent" to false
                )
            )
        )

        // Push data to Firestore
        db.collection("cities")
            .document("chennai")
            .set(hashMapOf(
                "floodedAreas" to floodedAreas,
                "disasterManagementServices" to disasterManagementServices,
                "emergencySupplyDrops" to emergencySupplyDrops
            ))
            .addOnSuccessListener {
                Log.d("Firestore", "Synthetic data created successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to create synthetic data", exception)
            }
    }



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Button(onClick = {
        retrieveData()
    }) {
        Text("Retrieve Data")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FloodsafeTheme {
        Greeting("Android")
    }
}

}