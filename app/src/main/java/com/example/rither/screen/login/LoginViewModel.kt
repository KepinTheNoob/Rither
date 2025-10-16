package com.example.rither.screen.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(user = currentUser)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email : String, password : String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            // Update Firestore verified field
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .update("verified", true)

                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            auth.signOut()
                            _authState.value = AuthState.Info("Please verify your email before logging in.")
                        }
                    } else {
                        _authState.value = AuthState.Error("User not found.")
                    }
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Something went wrong"
                    )
                }
            }
    }
}

sealed class AuthState {
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Info(val message: String) : AuthState()
    data class Error(val message : String) : AuthState()
}
