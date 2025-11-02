package com.example.rither.screen.signup

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rither.R
import com.example.rither.data.Screen
import com.example.rither.ui.theme.SkylineOnSurface
import com.example.rither.ui.theme.SkylinePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri.encode as uriEncode

@Composable
fun SignupScreen(
    signupViewModel: SignupViewModel = viewModel(),
    navController: NavController,
    name: String = "",
    studentId: String = "",
    binusianId: String = "",
    university: String = ""
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- editable fields (user input) ---
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // --- errors ---
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    // --- profile image (optional) ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val authState = signupViewModel.authState.observeAsState()
//    LaunchedEffect(authState.value) {
//        when (val s = authState.value) {
//            is AuthState.Authenticated -> {
//                // Already authenticated — go to verify (or main) as you prefer
//                navController.navigate(Screen.VerifyEmail.name) {
//                    popUpTo(Screen.Signup.name) { inclusive = true }
//                }
//            }
//            is AuthState.Info -> {
//                val message = s.message
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//
//                val uid = FirebaseAuth.getInstance().currentUser?.uid
//                if (!uid.isNullOrBlank()) {
//                    navController.navigate(Screen.VerifyEmail.name) {
//                        popUpTo(Screen.Signup.name) { inclusive = true }
//                    }
//                } else {
//                    // fallback navigation
//                    navController.navigate(Screen.VerifyEmail.name) {
//                        popUpTo(Screen.Signup.name) { inclusive = true }
//                    }
//                }
//            }
//            is AuthState.Error -> {
//                val message = s.message
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//            }
//            else -> Unit
//        }
//    }

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticated -> {
                // The user is already authenticated and verified
                navController.navigate(Screen.VerifyEmail.name) {
                    popUpTo(Screen.Signup.name) { inclusive = true }
                }
            }

            is AuthState.Info -> {
                // Informational message (e.g. verification email sent)
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()

                // If the user has been created but not verified yet, go to verify screen
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (!uid.isNullOrBlank()) {
                    navController.navigate(Screen.Home.name) {
                        popUpTo(Screen.Signup.name) { inclusive = true }
                    }
                }
            }

            is AuthState.Error -> {
                // Show error message only, no navigation
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
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

                    Column(horizontalAlignment = Alignment.Start) {
                        // --- Read-only OCR fields ---
                        OutlinedTextField(
                            value = name,
                            onValueChange = { /* no-op */ },
                            label = { Text("Full Name (from card)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon")
                            },
                            singleLine = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylinePrimary,
                                unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                                focusedLabelColor = SkylinePrimary,
                                unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                                cursorColor = SkylinePrimary,
                                focusedContainerColor = Color(  0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = studentId,
                            onValueChange = { /* no-op */ },
                            label = { Text("Student ID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false,
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
                            value = binusianId,
                            onValueChange = { /* no-op */ },
                            label = { Text("Binusian ID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false,
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
                            value = university,
                            onValueChange = { /* no-op */ },
                            label = { Text("University") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false,
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Button to open CameraScreen and run OCR
                        Button(
                            onClick = {
                                navController.navigate(Screen.Camera.name)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scan Student Card")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- Editable fields ---
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = ""
                            },
                            label = { Text("Email") },
                            placeholder = { Text("example@binus.ac.id") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            isError = emailError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylinePrimary,
                                unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                                focusedLabelColor = SkylinePrimary,
                                unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                                cursorColor = SkylinePrimary,
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            ),
                            supportingText = {
                                if (emailError.isNotEmpty()) Text(emailError, color = MaterialTheme.colorScheme.error)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                phoneError = ""
                            },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Call, contentDescription = "Phone Icon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = phoneError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylinePrimary,
                                unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                                focusedLabelColor = SkylinePrimary,
                                unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                                cursorColor = SkylinePrimary,
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            ),
                            supportingText = {
                                if (phoneError.isNotEmpty()) Text(phoneError, color = MaterialTheme.colorScheme.error)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = ""
                            },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon") },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = "Toggle password")
                                }
                            },
                            isError = passwordError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylinePrimary,
                                unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                                focusedLabelColor = SkylinePrimary,
                                unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                                cursorColor = SkylinePrimary,
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            ),
                            supportingText = {
                                if (passwordError.isNotEmpty()) Text(passwordError, color = MaterialTheme.colorScheme.error)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError = ""
                            },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm Icon") },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            isError = confirmPasswordError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylinePrimary,
                                unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                                focusedLabelColor = SkylinePrimary,
                                unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                                cursorColor = SkylinePrimary,
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            ),
                            supportingText = {
                                if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError, color = MaterialTheme.colorScheme.error)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            emailError = if (email.isBlank()) "Email is required!" else if (!email.contains("@")) "Email must contain '@'!" else ""
                            phoneError = if (phone.isBlank()) "Phone number is required!" else if (phone.length < 7) "Phone must have at least 7 digits!" else ""
                            passwordError = if (password.isBlank()) "Password is required!" else if (password.length < 8) "Password must have at least 8 characters!" else ""
                            confirmPasswordError = if (confirmPassword.isBlank()) "Confirm is required!" else if (confirmPassword != password) "Must match password!" else ""

                            if (emailError.isEmpty() && phoneError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                                if (email.endsWith("@binus.ac.id", ignoreCase = true)) {
                                    // Use the OCR name when available; fallback to empty string (VM will error if empty)
                                    val nameToSave = if (name.isNotBlank()) name else ""

                                    signupViewModel.signup(
                                        email = email,
                                        phone = phone,
                                        password = password,
                                        name = nameToSave,
                                        studentId,
                                        binusianId,
                                        imageUri,
                                        context
                                    )
                                    // The LaunchedEffect listening to authState will handle Firestore extra fields & navigation
                                } else {
                                    emailError = "Email must end with @binus.ac.id"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Text("Create Account", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Already have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = { navController.navigate(Screen.Login.name) }) {
                            Text("Log in")
                        }
                    }
                }
            }
        }
    }
}
