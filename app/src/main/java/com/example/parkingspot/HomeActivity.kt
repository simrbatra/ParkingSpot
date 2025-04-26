package com.example.parkingspot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.parkingspot.databinding.ActivityHomeBinding
import com.example.parkingspot.model.ParkingSpot
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var map: GoogleMap
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Set the click listener for the View Favorites button
        binding.viewFavoritesBtn.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        loadParkingSpots()
    }

    private fun enableMyLocation() {
        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        map.isMyLocationEnabled = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

                // Enable long-click to save custom parking spots
                map.setOnMapLongClickListener { latLng ->
                    saveParkingSpot("Custom Spot", latLng)
                }
            }
        }
    }

    private fun saveParkingSpot(name: String, latLng: LatLng) {
        val spot = ParkingSpot(name, latLng.latitude, latLng.longitude)
        firestore.collection("spots").add(spot)
            .addOnSuccessListener {
                Toast.makeText(this, "Spot Saved!", Toast.LENGTH_SHORT).show()
                map.addMarker(MarkerOptions().position(latLng).title(name))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadParkingSpots() {
        firestore.collection("spots")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val spot = document.toObject(ParkingSpot::class.java)
                    val latLng = LatLng(spot.latitude, spot.longitude)
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(spot.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(
                                if (spot.isBooked) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_GREEN
                            ))
                    )

                    marker?.tag = document.id
                }

                // Set the click listener for markers
                map.setOnMarkerClickListener { marker ->
                    val docId = marker.tag as? String
                    docId?.let {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(marker.title)
                        builder.setItems(arrayOf("Book Spot", "Save as Favorite")) { _, which ->
                            when (which) {
                                0 -> {
                                    // Book the spot
                                    firestore.collection("spots").document(it)
                                        .update("isBooked", true)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Spot Booked!", Toast.LENGTH_SHORT).show()
                                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                                            val intent = Intent(this, PaymentActivity::class.java)
                                            intent.putExtra("spotName", marker.title)
                                            startActivity(intent)
                                        }
                                }
                                1 -> {
                                    // Save as favorite
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userId != null) {
                                        firestore.collection("spots").document(it)
                                            .update("favoritedBy", FieldValue.arrayUnion(userId))
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Toast.makeText(this, "Please log in to save as favorite", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        builder.show()
                    }
                    true
                }
            }
    }
}
