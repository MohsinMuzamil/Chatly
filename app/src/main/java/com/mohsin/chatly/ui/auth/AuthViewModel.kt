package com.mohsin.chatly.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.mohsin.chatly.data.model.User

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow(auth.currentUser != null)
    val authState: StateFlow<Boolean> = _authState


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Attempt login with $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Login success")
                    _authState.value = true
                } else {
                    Log.e("AuthViewModel", "Login failure", task.exception)
                    _error.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun register(displayName: String, mobile: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _authState.value = true


                    val userObj = User(
                        uid = user?.uid ?: "",
                        email = user?.email ?: "",
                        displayName = displayName,
                        mobile = mobile,
                        photoUrl = user?.photoUrl.toString()

                    )
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userObj.uid)
                        .set(userObj)
                        .addOnFailureListener { e ->
                            _error.value = "Failed to save user: ${e.localizedMessage}"
                        }
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Registration failed"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = false
    }


}
