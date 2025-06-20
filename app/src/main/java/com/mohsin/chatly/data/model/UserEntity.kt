package com.mohsin.chatly.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String?,
    val mobile: String,
    val photoUrl: String?,
    val dob: String,
    val gender: String
) {
    fun toUser(): User = User(uid, email, displayName, mobile, photoUrl, dob, gender)
    companion object {
        fun fromUser(user: User) = UserEntity(
            user.uid, user.email, user.displayName, user.mobile, user.photoUrl, user.dob, user.gender
        )
    }
}