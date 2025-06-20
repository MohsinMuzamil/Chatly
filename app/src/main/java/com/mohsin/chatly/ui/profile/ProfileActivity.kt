package com.mohsin.chatly.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mohsin.chatly.R
import com.mohsin.chatly.data.model.User
import com.mohsin.chatly.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    // Use nullable User to avoid lateinit issues
    private var currentUser: User? = null

    private val editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedUser = result.data?.getParcelableExtra<User>("updated_user")
            updatedUser?.let {
                currentUser = it
                updateUi(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.user.observe(this) { user ->
            user?.let {
                currentUser = it
                updateUi(it)
                binding.btnEditProfile.setOnClickListener { _ ->
                    // Always use the latest user
                    val intent = Intent(this, EditProfileActivity::class.java)
                    intent.putExtra("user", currentUser)
                    editProfileLauncher.launch(intent)
                }
            }
        }

        // Optional: Pull to refresh or auto-refresh
        lifecycleScope.launchWhenStarted { viewModel.refreshProfile() }
    }

    private fun updateUi(user: User) {
        binding.tvName.text = user.displayName
        binding.tvEmail.text = user.email
        binding.tvMobile.text = user.mobile
        binding.tvDob.text = user.dob
        binding.tvGender.text = user.gender

        if (!user.photoUrl.isNullOrEmpty()) {
            Glide.with(this).load(user.photoUrl).circleCrop().into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }
}