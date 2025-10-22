package com.example.rither.screen.signup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", "dz9p6wwzt",
            "api_key", "723678195572612",
            "api_secret", "97W4VIBdW1TDh8fea66oG6waYmw"
        )
    )

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl
    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val user = auth.currentUser
        if (user != null) {
            // Construct the image URL
            _imageUrl.value = "https://res.cloudinary.com/dz9p6wwzt/image/upload/${user.uid}"

            // Fetch user's name from Firestore
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    _userName.value = document.getString("name")
                }
                .addOnFailureListener {
                    _userName.value = "User" // Fallback name
                }
        }
    }

    fun uploadProfileImage(uri: Uri, context: Context) {
        val user = auth.currentUser ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }

                val options = ObjectUtils.asMap(
                    "public_id", user.uid,
                    "overwrite", true,
                    "resource_type", "image"
                )

                val uploadResult = cloudinary.uploader().upload(tempFile, options)
                tempFile.delete()

                val newUrl = uploadResult["secure_url"] as? String
                if (newUrl != null) {
                    withContext(Dispatchers.Main) {
                        // Use a cache-busting trick to force UI recomposition
                        _imageUrl.value = "$newUrl?t=${System.currentTimeMillis()}"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle upload error (e.g., show a toast)
            }
        }
    }
}