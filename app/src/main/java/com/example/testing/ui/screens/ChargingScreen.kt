package com.example.testing.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.ui.components.formatTime
import com.example.testing.ui.theme.TestingTheme
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ChargingScreen(onLogout: () -> Unit, onGoToPayCalculator: () -> Unit) {
    var isCharging by rememberSaveable { mutableStateOf(false) }
    var secondsElapsed by rememberSaveable { mutableLongStateOf(0L) }
    var totalCost by rememberSaveable { mutableDoubleStateOf(0.0) }
    val pricePerHour = 10.0

    LaunchedEffect(isCharging) {
        if (isCharging) {
            val startTime = System.currentTimeMillis() - (secondsElapsed * 1000)
            while (isCharging) {
                delay(200)
                val currentMillis = System.currentTimeMillis()
                secondsElapsed = (currentMillis - startTime) / 1000
                
                if (secondsElapsed >= 36000) {
                    secondsElapsed = 36000
                    isCharging = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            }
        }

        if (totalCost > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Last Session Cost",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "₹%.2f", totalCost),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(100.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = if (isCharging) "⚡ CHARGING IN PROGRESS" else "CHARGER READY",
            style = MaterialTheme.typography.titleMedium,
            color = if (isCharging) Color(0xFF4CAF50) else Color.Gray,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(260.dp)
                .border(
                    width = 10.dp,
                    color = if (isCharging) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCharging) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF10B981),
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )
            }
            Text(
                text = formatTime(secondsElapsed),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                ),
                color = if (isCharging) MaterialTheme.colorScheme.primary else Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { 
                    secondsElapsed = 0L 
                    isCharging = true 
                },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                enabled = !isCharging,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("CHARGE", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    isCharging = false
                    totalCost = (secondsElapsed / 3600.0) * pricePerHour
                },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                enabled = isCharging,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Power, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("STOP", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructional Text
        Text(
            text = "After charge is completed go for pay calculator",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pay Calculator Button
        Button(
            onClick = onGoToPayCalculator,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(16.dp),
            enabled = !isCharging // Allow only when not charging
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("PAY CALCULATOR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChargingScreenPreview() {
    TestingTheme {
        ChargingScreen(onLogout = {}, onGoToPayCalculator = {})
    }
}
