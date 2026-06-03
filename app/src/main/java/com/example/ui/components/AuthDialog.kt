package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun AuthDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputField by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }

    // First time setup fields
    var uName by remember { mutableStateOf("") }
    var uAge by remember { mutableStateOf("") }
    var uGender by remember { mutableStateOf("Female") }

    val genders = listOf("Female", "Male", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF4F46E5)
                )
                Text(
                    text = if (viewModel.isOtpSent) "Enter Authorization OTP" else "Secure Registration",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!viewModel.isOtpSent) {
                    Text(
                        text = "Access secure order checkouts and verified boutiques by logging in with your phone or email.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    OutlinedTextField(
                        value = inputField,
                        onValueChange = { inputField = it },
                        placeholder = { Text("Phone Number or Email") },
                        label = { Text("Identifier") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_username_field"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Helper label showing Master Admin hint
                    Text(
                        text = "💡 Enter bbhola099@gmail.com (or similar) to test user routes, or mbhola099@gmail.com for Master Admin credentials.",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                } else {
                    // OTP Sent Panel
                    Text(
                        text = "A dynamic high-security verification passcode was dispatched to ${viewModel.verificationPhoneOrEmail}.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    // Display code directly for testing ease!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2CC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔑 SIMULATED SMS GATEWAY API INTEGRATION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB25E00)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your Secure OTP is: ${viewModel.generatedOtp}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("6-Digit Passcode") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_otp_field"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Divider()

                    Text(
                        text = "Complete Profile (Required for first-time login):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = uName,
                        onValueChange = { uName = it },
                        label = { Text("Buyer Name") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_setup"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uAge,
                            onValueChange = { uAge = it },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(imageVector = Icons.Default.Cake, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("Gender Selector:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                genders.forEach { g ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (uGender == g) Color(0xFF4F46E5) else Color(0xFFF3F2EE)
                                            )
                                            .clickable { uGender = g }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = g.take(1),
                                            color = if (uGender == g) Color.White else Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!viewModel.isOtpSent) {
                Button(
                    onClick = {
                        if (inputField.trim().isEmpty()) return@Button
                        viewModel.initiateLogin(inputField)
                        Toast.makeText(context, "Verification dynamic OTP Sent!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    modifier = Modifier.testTag("submit_auth_step1")
                ) {
                    Text("Request OTP")
                }
            } else {
                Button(
                    onClick = {
                        if (enteredOtp.trim().isEmpty() || uName.trim().isEmpty()) {
                            Toast.makeText(context, "Please enter OTP and complete profile name.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val success = viewModel.verifyOtp(
                            otp = enteredOtp.trim(),
                            name = uName,
                            age = uAge,
                            gender = uGender
                        )

                        if (success) {
                            Toast.makeText(context, "Logged in as ${viewModel.currentUser?.name} successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Verification code invalid. Double check UTR passcode helper.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    modifier = Modifier.testTag("submit_auth_step2")
                ) {
                    Text("Verify & Setup")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.isOtpSent = false
                    onDismiss()
                }
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
