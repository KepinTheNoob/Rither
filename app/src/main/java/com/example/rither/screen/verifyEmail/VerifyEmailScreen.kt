package com.example.rither.screen.verifyEmail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rither.data.Screen
import com.example.rither.ui.theme.RitherTheme
import com.example.rither.ui.theme.SkylinePrimary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val snackbarHostState = remember { SnackbarHostState() }  // <-- use this
    val scope = rememberCoroutineScope()
    var infoMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // <-- Material3
        topBar = {
            TopAppBar(title = { Text("Verify Your Email") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "A verification email has been sent to:",
                    fontSize = 16.sp
                )
                Text(
                    text = user?.email ?: "",
                    fontSize = 18.sp,
                    color = SkylinePrimary
                )
                Text(
                    text = "Please verify your email before logging in.",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (user != null && !isSending) {
                            isSending = true
                            user.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    isSending = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (task.isSuccessful) "Verification email sent successfully."
                                            else "Failed to send: ${task.exception?.message}"
                                        )
                                    }
                                }
                        }
                    },
                    enabled = !isSending
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Resend"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Resend Email")
                }

                Button(
                    onClick = {
                        user?.reload()?.addOnCompleteListener {
                            if (user.isEmailVerified) {
                                navController.navigate(Screen.Home.name) {
                                    popUpTo(Screen.VerifyEmail.name) { inclusive = true }
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Email not verified yet. Please check your inbox.")
                                }
                            }
                        }
                    }
                ) {
                    Text("I've Verified")
                }
            }
        }
    }
}
