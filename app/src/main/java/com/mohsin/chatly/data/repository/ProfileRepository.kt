package com.mohsin.chatly.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mohsin.chatly.data.local.UserDao
import com.mohsin.chatly.data.model.User
import com.mohsin.chatly.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getUserFlow(): Flow<User?> {
        val uid = getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.getUser(uid).map { it?.toUser() }
    }

    suspend fun fetchUserFromRemoteAndCache() {
        val uid = getCurrentUserId() ?: return
        val doc = firestore.collection("users").document(uid).get().await()
        doc.toObject(User::class.java)?.let { user ->
            userDao.insertUser(UserEntity.fromUser(user))
        }
    }

    suspend fun updateUser(user: User): String? {
        val userId = user.uid
        if (userId.isBlank()) throw IllegalArgumentException("User ID is blank!")

        // Just use the photoUrl as set in the User object (from Cloudinary)
        val updatedUser = user

        // Update Firestore
        firestore.collection("users").document(userId).set(updatedUser).await()

        // Update local Room database
        userDao.insertUser(UserEntity.fromUser(updatedUser))

        return updatedUser.photoUrl
    }
}