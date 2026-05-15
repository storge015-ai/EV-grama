package com.example.testing.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun LogoHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.TopEnd) {
            Text(
                text = "EV",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00DC7F),
                    fontSize = 72.sp,
                    letterSpacing = (-2).sp
                )
            )
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color(0xFF00DC7F),
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = 12.dp, y = 8.dp)
            )
        }
        Text(
            text = "Yes it's a Silent Start",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.offset(y = (-8).dp)
        )
    }
}

@Composable
fun AuthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp) },
            trailingIcon = { Icon(trailingIcon, null, tint = Color.White.copy(alpha = 0.4f)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F1818),
                unfocusedContainerColor = Color(0xFF0F1818),
                focusedBorderColor = Color(0xFF00DC7F),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF00DC7F),
            ),
            singleLine = true
        )
    }
}

@Composable
fun EVIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00DC7F).copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(width = 180.dp, height = 90.dp)
                    .background(Color(0xFF0A1212), RoundedCornerShape(topStart = 40.dp, topEnd = 20.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 40.dp, topEnd = 20.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(35.dp, 10.dp)
                        .background(Color(0xFF00DC7F).copy(alpha = 0.6f), CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 150.dp)
                    .background(Color(0xFF121B1B), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(25.dp).background(Color(0xFF00DC7F).copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Bolt, null, tint = Color(0xFF00DC7F), modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
}

@Preview(showBackground = true, backgroundColor = 0xFF091010)
@Composable
fun LogoHeaderPreview() {
    MaterialTheme {
        LogoHeader()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF091010)
@Composable
fun AuthInputFieldPreview() {
    MaterialTheme {
        AuthInputField(
            label = "Email",
            value = "",
            onValueChange = {},
            placeholder = "Enter your email",
            trailingIcon = Icons.Default.Email
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF091010)
@Composable
fun EVIllustrationPreview() {
    MaterialTheme {
        EVIllustration()
    }
}
