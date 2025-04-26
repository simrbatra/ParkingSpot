package com.example.parkingspot



import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingspot.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spotName = intent.getStringExtra("spotName") ?: "Unknown Spot"
        binding.spotName.text = "Parking Spot: $spotName"
        binding.amount.text = "₹ 50.00"

        binding.payBtn.setOnClickListener {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
