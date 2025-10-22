package com.example.rither.screen.signup

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rither.R
import com.example.rither.data.Screen
import com.example.rither.ui.theme.RitherTheme
import com.example.rither.ui.theme.SkylineOnSurface
import com.example.rither.ui.theme.SkylinePrimary

@Composable
fun SignupScreen(
    signupViewModel: SignupViewModel = SignupViewModel(),
    navController: NavController
) {
    var showPassword by remember { mutableStateOf(value = false) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // State for the selected image URI

    val authState = signupViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate(Screen.VerifyEmail.name) {
                popUpTo(Screen.Signup.name) { inclusive = true }
            }

            is AuthState.Info -> {
                val message = (authState.value as AuthState.Info).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                navController.navigate(Screen.VerifyEmail.name) {
                    popUpTo(Screen.Signup.name) { inclusive = true }
                }
            }

            is AuthState.Error -> {
                val message = (authState.value as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Use a Box to center the card on the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(all = 24.dp)
                        .background(color = MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create your account",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Join the Rither community",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Profile Picture Selector ---
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = imageUri ?: R.drawable.ic_launcher_background
                            ),
                            contentDescription = "User Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (imageUri == null) {
                            Text("Add Photo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // --- End of Profile Picture Selector ---

                    Column(horizontalAlignment = Alignment.Start) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = ""
                            },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Person Icon"
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
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
                            isError = nameError.isNotEmpty(),
                            supportingText = {
                                if (nameError.isNotEmpty()) {
                                    Text(nameError, color = MaterialTheme.colorScheme.error)
                                }
                            },
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call Icon"
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
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
                            )
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

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                passwordError = ""
                            },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirm Password Icon"
                                )
                            },
                            visualTransformation = if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
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
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylinePrimary,
                                unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                                focusedLabelColor = SkylinePrimary,
                                unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                                cursorColor = SkylinePrimary,
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            ),
                            isError = confirmPasswordError.isNotEmpty(),
                            supportingText = {
                                if (confirmPasswordError.isNotEmpty()) {
                                    Text(confirmPasswordError, color = MaterialTheme.colorScheme.error)
                                }
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            emailError =
                                if(email.isBlank()) "Email is required!"
                                else if(!email.contains("@")) "Email must contain '@'!"
                                else ""
                            passwordError =
                                if(password.isBlank()) "Password is required!"
                                else if(password.length < 8) "Password must have at least 8 characters!"
                                else ""
                            nameError =
                                if(name.isBlank()) "Name is required!"
                                else ""
                            phoneError =
                                if(phone.isBlank()) "Phone number is required!"
                                else if(phone.length < 7) "Phone number must have at least 8 characters!"
                                else ""
                            confirmPasswordError =
                                if(confirmPassword.isBlank()) "Confirm is required!"
                                else if(confirmPassword != password) "Must be same with password!"
                                else ""
                            if(
                                emailError.isEmpty() && passwordError.isEmpty() &&
                                nameError.isEmpty() &&
                                phoneError.isEmpty() &&
                                confirmPasswordError.isEmpty()
                            ) {
                                if (email.endsWith("@binus.ac.id", ignoreCase = true)) {
                                    signupViewModel.signup(
                                        email = email,
                                        password = password,
                                        name = name,
                                        phone = phone,
                                        imageUri = imageUri,
                                        context = context
                                    )
                                } else {
                                    emailError = "Email must end with @binus.ac.id"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Create Account",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { navController.navigate(Screen.Login.name) }) {
                            Text("Log in")
                        }
                    }
                }
            }
        }
    }
}