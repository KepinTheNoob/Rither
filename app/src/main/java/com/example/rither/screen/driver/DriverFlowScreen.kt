package com.example.rither.screen.driver

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rither.data.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverFlowScreen(
    navController: NavController
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3

    val titles = listOf(
        "Setup Driver Profile",
        "Vehicle Details",
        "Payment Information"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        titles[currentStep - 1],
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Empty space to balance the title
                    Spacer(Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            BottomButton(
                onClick = {
                    if (currentStep < totalSteps) currentStep++ else {
                        navController.navigate(Screen.DriverSubmit.name)
                    }
                },
                text = if (currentStep < totalSteps) "Next" else "Finish"
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            StepIndicator(currentStep = currentStep, totalSteps = totalSteps)
            Spacer(Modifier.height(24.dp))

            // Content switches based on the current step
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                when (currentStep) {
                    1 -> SetupProfileScreen()
                    2 -> VehicleDetailsScreen()
                    3 -> PaymentInfoScreen()
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val isCurrent = i == currentStep
            val color = if (isCurrent) MaterialTheme.colorScheme.tertiary else Color.LightGray
            val size = if (isCurrent) 10.dp else 8.dp
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color, CircleShape)
            )
        }
    }
}

// --- Screen 1: Setup Driver Profile ---
@Composable
fun SetupProfileScreen() {
    var studentId by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = "Student ID",
            placeholder = "28022XXXXX"
        )
        FormTextField(
            value = destination,
            onValueChange = { destination = it },
            label = "Add your preferred destination",
            placeholder = "Jl. Sudirman"
        )
        FileUploadBox(label = "Upload student ID card")

        Text(
            text = "or",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { /* TODO: Open Camera */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = "Open Camera",
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Open Camera & Take Photo",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "To verify your identity, you'll need to upload your student ID card. Your data will stay safe and private.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

// --- Screen 2: Vehicle Details ---
@Composable
fun VehicleDetailsScreen() {
    var licenseNumber by remember { mutableStateOf("") }
    var brandModel by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Vehicle Type", fontWeight = FontWeight.SemiBold)
        VehicleTypeSelector()

        FormTextField(
            value = licenseNumber,
            onValueChange = { licenseNumber = it },
            label = "Vehicle license number",
            placeholder = "X 0000 XXX"
        )
        FormDropdown(
            label = "Color of Vehicle",
            options = listOf("Green", "Black", "White", "Red", "Blue"),
            selectedOption = "Green",
            onOptionSelected = {}
        )
        FormTextField(
            value = brandModel,
            onValueChange = { brandModel = it },
            label = "Vehicle brand & model",
            placeholder = "Honda Amaze"
        )
        FileUploadBox(label = "Upload vehicle photo")

        Spacer(Modifier.height(8.dp))
        Text(
            text = "To verify your identity, please upload a picture of the front of your car.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun VehicleTypeSelector() {
    var selectedType by remember { mutableStateOf("Scooter") }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VehicleTypeButton(
            text = "Scooter",
            icon = Icons.Default.TwoWheeler,
            isSelected = selectedType == "Scooter",
            onClick = { selectedType = "Scooter" },
            modifier = Modifier.weight(1f)
        )
        VehicleTypeButton(
            text = "Car",
            icon = Icons.Default.DirectionsCar,
            isSelected = selectedType == "Car",
            onClick = { selectedType = "Car" },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleTypeButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.White
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiary else Color.Black
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = text)
            Spacer(Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
}

// --- Screen 3: Payment Information ---
@Composable
fun PaymentInfoScreen() {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var vcc by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // --- Credit Card Visual ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A296D) // Dark Blue
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Icon(
                    Icons.Default.CalendarToday, // Placeholder for chip
                    contentDescription = "Card Chip",
                    tint = Color.LightGray,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Gray, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    ".... .... .... ....",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(16.dp))
                Text("MM/YY", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                // Bottom green part
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .offset(x = (-20).dp, y = (20).dp), // Break out of card padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.White.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp)) // Extra space for card
        Text(
            "Card Detail",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )

        FormTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            placeholder = "0000 0000 0000 0000",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                // Placeholder for VISA/Mastercard logos
                Row {
                    Box(
                        Modifier
                            .size(30.dp, 20.dp)
                            .background(Color(0xFFF9A000), RoundedCornerShape(2.dp))
                    )
                    Spacer(Modifier.width(4.dp))
                    Box(
                        Modifier
                            .size(30.dp, 20.dp)
                            .background(Color(0xFFE60012), RoundedCornerShape(2.dp))
                    )
                }
            }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FormTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                placeholder = "Expiry date",
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Expiry") },
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = vcc,
                onValueChange = { vcc = it },
                placeholder = "VCC",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "VCC") },
                modifier = Modifier.weight(1f)
            )
        }
        FormTextField(
            value = cardHolder,
            onValueChange = { cardHolder = it },
            placeholder = "Card holder"
        )
        FormDropdown(
            options = listOf("Indonesia", "USA", "Singapore"),
            selectedOption = "Indonesia",
            onOptionSelected = {}
        )

        Text(
            text = "By providing your card information, you allow us to charge your card for future payments in accordance with their terms.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}


// --- Common Reusable Composables ---

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (label != null) {
            Text(label, fontWeight = FontWeight.SemiBold)
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdown(
    label: String? = null,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (label != null) {
            Text(label, fontWeight = FontWeight.SemiBold)
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FileUploadBox(
    label: String,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF7F7F7))
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    RoundedCornerShape(12.dp)
                )
                .clickable { /* TODO: Handle file pick */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "Upload",
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
                Text("Select file", color = Color.Gray)
            }
        }
    }
}

@Composable
fun BottomButton(
    onClick: () -> Unit,
    text: String,
) {
    // This column ensures the button respects safe area insets
    // and adds padding.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White
            )
        ) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}