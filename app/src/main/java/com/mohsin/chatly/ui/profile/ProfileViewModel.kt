package com.mohsin.chatly.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.mohsin.chatly.data.local.AppDatabase
import com.mohsin.chatly.data.model.User
import com.mohsin.chatly.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repo = ProfileRepository(db.userDao())
    val user: LiveData<User?> = repo.getUserFlow().asLiveData()

    val displayName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val mobile = MutableLiveData<String>()
    val photoUrl = MutableLiveData<String?>()
    val dob = MutableLiveData<String>()
    val gender = MutableLiveData<String>()

    val saveStatus = MutableLiveData<Boolean>()
    val errorMsg = MutableLiveData<String>()
    val profileImageUri = MutableLiveData<Uri?>()

    fun loadProfileForEdit(user: User?) {
        if (user == null) return
        displayName.value = user.displayName ?: ""
        email.value = user.email ?: ""
        mobile.value = user.mobile ?: ""
        photoUrl.value = user.photoUrl
        dob.value = user.dob ?: ""
        gender.value = user.gender ?: ""
    }

    fun refreshProfile() {
        viewModelScope.launch {
            repo.fetchUserFromRemoteAndCache()
        }
    }

    fun saveProfile() {
        val updatedUser = User(
            uid = repo.getCurrentUserId() ?: "",
            displayName = displayName.value ?: "",
            email = email.value ?: "",
            mobile = mobile.value ?: "",
            photoUrl = photoUrl.value, // Cloudinary URL here!
            dob = dob.value ?: "",
            gender = gender.value ?: ""
        )
        viewModelScope.launch {
            try {
                repo.updateUser(updatedUser)
                saveStatus.postValue(true)
            } catch (e: Exception) {
                errorMsg.postValue(e.localizedMessage ?: "Unknown error")
                saveStatus.postValue(false)
            }
        }
    }

    fun getCurrentUser(): User {
        return User(
            uid = repo.getCurrentUserId() ?: "",
            email = email.value ?: "",
            displayName = displayName.value ?: "",
            mobile = mobile.value ?: "",
            photoUrl = photoUrl.value,
            dob = dob.value ?: "",
            gender = gender.value ?: ""
        )
    }
}