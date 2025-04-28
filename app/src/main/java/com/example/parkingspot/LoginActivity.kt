package com.example.parkingspot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingspot.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is signed in
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            // No user is signed in
            Log.d("AuthCheck", "No user logged in")


            binding.loginBtn.setOnClickListener {
                val email = binding.emailEt.text.toString()
                val password = binding.passwordEt.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            Log.e("inside", "" + it.isSuccessful)
                            if (it.isSuccessful) {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            binding.signupRedirect.setOnClickListener {
                startActivity(Intent(this, SignupActivity::class.java))
            }
        }
    }
}
