package com.example.parkingspot

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingspot.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private val UPI_PAYMENT_REQUEST_CODE = 991

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spotName = intent.getStringExtra("spotName") ?: "Unknown Spot"
        binding.spotName.text = "Parking Spot: $spotName"
        binding.amount.text = "₹ 50.00"

        binding.payBtn.setOnClickListener {
            launchGooglePay(spotName)
        }
    }

    private fun launchGooglePay(spotName: String) {
        val uri = Uri.parse(
            "upi://pay?" +
                    "pa=simarbatra@oksbi" +
                    "&pn=ParkEase" +
                    "&tn=Parking for $spotName" +
                    "&am=50.00" +
                    "&cu=INR" +
                    "&tr=TXN${System.currentTimeMillis()}"
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }

        // Try to find Google Pay among UPI apps
        val upiApps = packageManager.queryIntentActivities(intent, 0)
        var gpayIntent: Intent? = null

        for (app in upiApps) {
            if (app.activityInfo.packageName.contains("com.google.android.apps.nbu.paisa.user")) {
                intent.setPackage(app.activityInfo.packageName)
                gpayIntent = intent
                break
            }
        }

        if (gpayIntent != null) {
            startActivityForResult(gpayIntent, UPI_PAYMENT_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Google Pay is not installed or not configured for UPI", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (Activity.RESULT_OK == resultCode && data != null) {
                val response = data.getStringExtra("response") ?: ""
                if (response.lowercase().contains("success")) {
                    Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
