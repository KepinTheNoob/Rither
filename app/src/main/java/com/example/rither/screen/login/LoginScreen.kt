package com.example.rither.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rither.ui.theme.RitherTheme
import com.example.rither.ui.theme.SkylineOnSurface
import com.example.rither.ui.theme.SkylinePrimary
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.rither.R
import com.example.rither.data.Screen


@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = LoginViewModel()
) {
    var showPassword by remember { mutableStateOf(value = false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val authState = loginViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate(Screen.Home.name) {
                popUpTo(Screen.Login.name) { inclusive = true }
            }
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Log in to Rither",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Welcome back! Let’s get you moving.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        label = {
                            Text( "Email" )
                        },
                        placeholder = { Text("example@binus.ac.id") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        ),
                        isError = emailError.isNotEmpty(),
                        supportingText = {
                            if (emailError.isNotEmpty()) {
                                Text(emailError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        label = {
                            Text("Password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon"
                            )
                        },
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            if (showPassword) {
                                IconButton(onClick = { showPassword = false }) {
                                    Icon(
                                        imageVector = Icons.Filled.Visibility,
                                        contentDescription = "hide_password"
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { showPassword = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = "hide_password"
                                    )
                                }
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        ),
                        isError = passwordError.isNotEmpty(),
                        supportingText = {
                            if (passwordError.isNotEmpty()) {
                                Text(passwordError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                    )

//                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { /* TODO: Forgot password */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot password?", color = SkylinePrimary)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            emailError = if(email.isBlank()) "Email is required!" else ""
                            passwordError = if(password.isBlank()) "Password is required!" else ""

                            if(emailError.isEmpty() && passwordError.isEmpty()) {
                                if (email.endsWith("@binus.ac.id", ignoreCase = true)) {
                                    loginViewModel.login(email.trim(), password)
                                } else {
                                    emailError = "Email must end with @binus.ac.id"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkylinePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = "Login Icon",
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text("Log in", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text("Login with Google", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New here?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { navController.navigate(Screen.Signup.name) }) {
                            Text("Create an account", color = SkylinePrimary)
                        }
                    }
                }
            }
        }
    }
}