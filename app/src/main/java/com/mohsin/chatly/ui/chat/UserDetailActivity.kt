package com.mohsin.chatly.ui.chat

import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import androidx.appcompat.app.AppCompatActivity
import com.mohsin.chatly.R
import com.mohsin.chatly.data.model.User

class UserDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_details_card)

        val photo = findViewById<ShapeableImageView>(R.id.ivUserDetailPhoto)
        val name = findViewById<TextView>(R.id.tvUserDetailName)
        val email = findViewById<TextView>(R.id.tvUserDetailEmail)
        val mobile = findViewById<TextView>(R.id.tvUserDetailMobile)
        val dob = findViewById<TextView>(R.id.tvUserDetailDOB)
        val gender = findViewById<TextView>(R.id.tvUserDetailGender)

        // Example: get User from intent or fetch from Firestore here
        val user = intent.getParcelableExtra<User>("user")
        if (user != null) {
            name.text = user.displayName
            email.text = user.email
            mobile.text = user.mobile
            dob.text = user.dob
            gender.text = user.gender
            if (!user.photoUrl.isNullOrEmpty()) {
                Glide.with(this).load(user.photoUrl).circleCrop().into(photo)
            } else {
                photo.setImageResource(R.drawable.ic_profile_placeholder)
            }
        }
    }
}