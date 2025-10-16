package com.example.rither.screen.verifyEmail

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmailVerificationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _infoMessage = MutableStateFlow("A verification email has been sent. Please check your inbox.")
    val infoMessage: StateFlow<String> = _infoMessage

    private val _isVerified = MutableStateFlow(false)
    val isVerified: StateFlow<Boolean> = _isVerified

    fun checkEmailVerified() {
        auth.currentUser?.reload()?.addOnSuccessListener {
            if (auth.currentUser?.isEmailVerified == true) {
                _infoMessage.value = "Your email is verified! You can continue."
                _isVerified.value = true
            } else {
                _infoMessage.value = "Email not verified yet. Please check your inbox."
            }
        }?.addOnFailureListener {
            _infoMessage.value = "Failed to check verification: ${it.message}"
        }
    }

    fun resendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                _infoMessage.value = "Verification email resent. Please check your inbox."
            }?.addOnFailureListener {
                _infoMessage.value = "Failed to resend email: ${it.message}"
            }
    }
}