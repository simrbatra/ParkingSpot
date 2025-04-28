package com.example.parkingspot


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingspot.databinding.ActivityFavoritesBinding
import com.example.parkingspot.model.ParkingSpot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var firestore: FirebaseFirestore
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()


            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        firestore.collection("spots")
            .whereArrayContains("favoritedBy", currentUserId ?: "")
            .get()
            .addOnSuccessListener { result ->
                val favorites = result.map { it.toObject(ParkingSpot::class.java) }
                val names = favorites.joinToString("\n") { it.name }

                binding.favoriteList.text = names.ifEmpty { "No favorites yet." }
            }
    }
}
