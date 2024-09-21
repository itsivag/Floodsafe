package com.code_crusaders.floodsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.code_crusaders.floodsafe.ui.theme.FloodsafeTheme
import com.google.firebase.firestore.FieldValue

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
        // Create a new flooded area document
        val newFloodedArea = hashMapOf(
            "location" to GeoPoint(12.975488684401213, 80.2206562864983),
            "depth" to 5,
            "radius" to 20
        )

        // Add the new flooded area to the array
        db.collection("cities")
            .document("chennai")
            .update("floodedAreas", FieldValue.arrayUnion(newFloodedArea))
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