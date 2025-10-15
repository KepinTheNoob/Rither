package com.example.rither.screen.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
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
                            _authState.value = AuthState.Authenticated
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
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Info(val message: String) : AuthState()
    data class Error(val message : String) : AuthState()
}