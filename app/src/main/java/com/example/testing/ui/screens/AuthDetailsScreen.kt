package com.example.testing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.ui.components.AuthInputField
import com.example.testing.ui.components.LogoHeader

@Composable
fun AuthDetailsScreen(
    isLoginMode: Boolean,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF091010)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                LogoHeader()
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = if (isLoginMode) "Login" else "Register",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00DC7F),
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Yes it's a Silent Start",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (!isLoginMode) {
                    AuthInputField(
                        label = "Full Name",
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Enter your full name",
                        trailingIcon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                AuthInputField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Enter your email",
                    trailingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(20.dp))

                AuthInputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password",
                    trailingIcon = Icons.Default.Lock,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF091010))
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    val brush = Brush.linearGradient(listOf(Color(0xFF00C853), Color(0xFF00E676)))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (isLoginMode) "SIGN IN" else "SIGN UP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginDetailsScreenPreview() {
    MaterialTheme {
        AuthDetailsScreen(isLoginMode = true, onSuccess = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "Login Invalid Data")
@Composable
fun LoginInvalidDataPreview() {
    MaterialTheme {
        AuthDetailsScreen(
            isLoginMode = true,
            onSuccess = {},
            onBack = {},
            errorMessage = "invalid data"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterDetailsScreenPreview() {
    MaterialTheme {
        AuthDetailsScreen(isLoginMode = false, onSuccess = {}, onBack = {})
    }
}
