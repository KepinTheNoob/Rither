package com.example.rither

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rither.ui.theme.RitherTheme
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : ComponentActivity() {

    public fun apiKey() {
        
        return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val auth : FirebaseAuth = FirebaseAuth.getInstance()

        val intent = intent
        val emailLink = intent.data.toString()

        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            val email = "someemail@domain.com"

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        // You can access the new user via result.getUser()
                        // Additional user info profile *not* available via:
                        // result.getAdditionalUserInfo().getProfile() == null
                        // You can check if the user is new or existing:
                        // result.getAdditionalUserInfo().isNewUser()
                    }
                }
        }

        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        enableEdgeToEdge()
        setContent {
            RitherTheme {
                MainScreen(
                    context = this,
                    activity = this,
                    intent = this
                )
            }
        }
    }
}