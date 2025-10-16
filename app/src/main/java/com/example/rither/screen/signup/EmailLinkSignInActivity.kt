package com.example.rither.screen.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.example.rither.R

class EmailLinkSignInActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the intent that started this activity
        handleIntent(intent)
    }


    private fun handleIntent(intent: Intent?) {
        if (intent != null) {
            val auth = Firebase.auth
            val emailLink = intent.data.toString()

            if (auth.isSignInWithEmailLink(emailLink)) {
                val email = "kevin.setiawan007@binus.ac.id"
                auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Handle successful sign-in
                        } else {
                            // Handle sign-in error
                        }
                    }
            }
        }
    }
}
