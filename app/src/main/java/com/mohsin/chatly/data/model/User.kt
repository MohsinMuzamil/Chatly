package com.mohsin.chatly.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String? = "",
    val mobile: String = "",
    val photoUrl: String? = null,
    val dob: String = "",
    val gender: String = ""
) : Parcelable