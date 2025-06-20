package com.mohsin.chatly.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mohsin.chatly.R
import com.mohsin.chatly.data.model.User
import com.mohsin.chatly.databinding.ActivityEditProfileBinding
import com.mohsin.chatly.util.CloudinaryUploader

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val CLOUDINARY_CLOUD_NAME = "your_cloud_name"
    private val CLOUDINARY_UNSIGNED_PRESET = "chatly_unsigned"

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.profileImageUri.value = it
            Glide.with(this).load(it).circleCrop().into(binding.ivProfile)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getParcelableExtra<User>("user")
        user?.let { viewModel.loadProfileForEdit(it) }

        binding.btnChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val imageUri = viewModel.profileImageUri.value
            if (imageUri != null) {
                // Upload to Cloudinary first
                CloudinaryUploader.uploadImage(
                    context = this,
                    imageUri = imageUri,
                    cloudName = CLOUDINARY_CLOUD_NAME,
                    unsignedPreset = CLOUDINARY_UNSIGNED_PRESET,
                    onSuccess = { imageUrl ->
                        runOnUiThread {
                            viewModel.photoUrl.value = imageUrl
                            viewModel.saveProfile() // This should save the imageUrl in Firestore
                        }
                    },
                    onFailure = { e ->
                        runOnUiThread {
                            Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } else {
                viewModel.saveProfile() // Save profile with existing or no photo
            }
        }

        val photoUrl = viewModel.photoUrl.value
        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this).load(photoUrl).circleCrop().into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
        }

        viewModel.saveStatus.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                // Return the updated User to the calling Activity
                val updatedUser = viewModel.getCurrentUser() // Implement this in ViewModel
                val resultIntent = Intent()
                resultIntent.putExtra("updated_user", updatedUser)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else if (success == false) {
                Toast.makeText(this, viewModel.errorMsg.value ?: "Save failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Bind fields
        binding.etName.setText(viewModel.displayName.value)
        binding.etMobile.setText(viewModel.mobile.value)
        binding.etDob.setText(viewModel.dob.value)
        binding.etGender.setText(viewModel.gender.value)
    }
}