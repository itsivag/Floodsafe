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
import com.code_crusaders.floodsafe.data.DataHandler
import com.code_crusaders.floodsafe.models.DisasterManagementService
import com.code_crusaders.floodsafe.models.EmergencySupplyDrop
import com.code_crusaders.floodsafe.models.FloodedArea
import com.code_crusaders.floodsafe.models.LocalDataStore
import com.code_crusaders.floodsafe.models.Notification
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
        sampleDataPrint()
    }

    private fun sampleDataPrint() {

        val localDataStore = DataHandler().getLocalSyntheticData()

        // Access data from the LocalDataStore object
        val floodedAreas = localDataStore.floodedAreas
        val disasterManagementServices = localDataStore.disasterManagementServices
        val emergencySupplyDrops = localDataStore.emergencySupplyDrops

        floodedAreas.forEach { floodedArea ->
            Log.d("MainActivity", "Flooded Area: ${floodedArea.location}, Depth: ${floodedArea.depth}, Radius: ${floodedArea.radius}")
        }
    }


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FloodsafeTheme {
        Greeting("Android")
    }
}

}