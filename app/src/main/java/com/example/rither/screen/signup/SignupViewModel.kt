package com.example.rither.screen.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if(auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        }
        else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun signup(email: String, name: String, phone: String, password: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        user.sendEmailVerification()
                            .addOnSuccessListener {
                                _authState.value = AuthState.Info("Verification email sent. Please verify before logging in.")

                                val db = FirebaseFirestore.getInstance()
                                val userData = hashMapOf(
                                    "email" to email,
                                    "name" to name,
                                    "phone" to phone,
                                    "verified" to false // mark as unverified
                                )

                                db.collection("users")
                                    .document(user.uid)
                                    .set(userData)
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error("Failed to send verification email: ${e.message}")
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Info(val message: String) : AuthState()
    data class Error(val message : String) : AuthState()
}