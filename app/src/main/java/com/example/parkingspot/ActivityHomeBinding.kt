package com.example.parkingspot.databinding

import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.parkingspot.R

class ActivityHomeBinding(
    val root: ConstraintLayout,
    val viewFavoritesBtn: Button
) {
    companion object {
        fun inflate(layoutInflater: android.view.LayoutInflater): ActivityHomeBinding {
            val view = layoutInflater.inflate(R.layout.activity_main, null, false)
            val root = view as ConstraintLayout
            val viewFavoritesBtn: Button = view.findViewById(R.id.viewFavoritesBtn)

            return ActivityHomeBinding(root, viewFavoritesBtn)
        }
    }
}
