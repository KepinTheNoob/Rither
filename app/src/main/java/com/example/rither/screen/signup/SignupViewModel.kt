package com.example.rither.screen.signup

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import android.content.Context // Import Context
import android.net.Uri // Import Uri
import kotlinx.coroutines.Dispatchers // Import Dispatchers
import kotlinx.coroutines.launch // Import launch
import kotlinx.coroutines.withContext // Import withContext
import java.io.File // Import File
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

class SignupViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Add Cloudinary instance
    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", "dz9p6wwzt",
            "api_key", "723678195572612",
            "api_secret", "97W4VIBdW1TDh8fea66oG6waYmw"
        )
    )

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState
    private val _emailVerificationMessage = MutableStateFlow("")
    val emailVerificationMessage: StateFlow<String> = _emailVerificationMessage

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val user = auth.currentUser
        if (user == null) {
            _authState.value = AuthState.Unauthenticated
        } else if (user.isEmailVerified) {
            _authState.value = AuthState.Authenticated(user)
        } else {
            _authState.value = AuthState.Info("Please verify your email")
        }
    }

//    fun signup(email: String, name: String, phone: String, password: String) {
//        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
//            _authState.value = AuthState.Error("All fields are required")
//            return
//        }
//
//        _authState.value = AuthState.Loading
//
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
//                    return@addOnCompleteListener
//                }
//
//                val user = auth.currentUser ?: run {
//                    _authState.value = AuthState.Error("Failed to get current user")
//                    return@addOnCompleteListener
//                }
//
//                // Write user data to Firestore
//                val db = FirebaseFirestore.getInstance()
//                val userData = hashMapOf(
//                    "email" to email,
//                    "name" to name,
//                    "phone" to phone,
//                    "verified" to false
//                )
//
//                db.collection("users")
//                    .document(user.uid)
//                    .set(userData)
//                    .addOnSuccessListener {
//                        user.sendEmailVerification()
//                            .addOnSuccessListener {
//                                _authState.value = AuthState.Info(
//                                    "Signup successful! Verification email sent."
//                                )
//                            }
//                            .addOnFailureListener { e ->
//                                _authState.value = AuthState.Error(
//                                    "User created but failed to send verification email: ${e.message}"
//                                )
//                            }
//                    }
//                    .addOnFailureListener { e ->
//                        _authState.value = AuthState.Error("Failed to save user data: ${e.message}")
//                    }
//            }
//    }

    fun signup(
        email: String,
        name: String,
        phone: String,
        password: String,
        imageUri: Uri?,
        context: Context
    ) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnSuccessListener {
                        _emailVerificationMessage.value =
                            "Verification email sent. Please check your inbox."

                        // Add user to Firestore
                        val userData = hashMapOf(
                            "email" to email, "name" to name, "phone" to phone, "verified" to false
                        )
                        db.collection("users").document(user.uid).set(userData)
                            .addOnSuccessListener {
                                // If an image was selected, upload it
                                if (imageUri != null) {
                                    uploadProfileImageToCloudinary(imageUri, user.uid, context)
                                }

                                // Send verification email regardless of image upload status
                                user.sendEmailVerification().addOnSuccessListener {
                                    _authState.value = AuthState.Info("Account created. Verify your email to continue.")
                                }.addOnFailureListener { e ->
                                    _authState.value = AuthState.Error("User created, but failed to send verification email: ${e.message}")
                                }
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error("Failed to save user data: ${e.message}")
                            }
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    private fun uploadProfileImageToCloudinary(uri: Uri, uid: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("signup_upload", ".jpg", context.cacheDir)
                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }

                val options = ObjectUtils.asMap(
                    "public_id", uid,
                    "overwrite", true,
                    "resource_type", "image"
                )

                cloudinary.uploader().upload(tempFile, options)
                tempFile.delete()
                // We don't need to do anything with the result URL on signup
                // as the URL is predictably based on the user's UID.
            } catch (e: Exception) {
                // Log the error, but don't block the user flow
                e.printStackTrace()
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