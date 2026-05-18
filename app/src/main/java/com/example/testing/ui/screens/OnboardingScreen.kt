package com.example.testing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.ui.theme.TestingTheme

@Composable
fun OnboardingScreen(
    title: String,
    subtitle: String,
    image: ImageVector,
    pageIndex: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    isLastPage: Boolean = false
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Illustration placeholder
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(Color(0xFFF0FDF4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = image,
                    contentDescription = null,
                    modifier = Modifier.size(140.dp),
                    tint = Color(0xFF84A933)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Page Indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { index ->
                    val color = if (index == pageIndex) Color(0xFF84A933) else Color(0xFFE5E7EB)
                    val width = if (index == pageIndex) 32.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(width = width, height = 8.dp)
                            .background(color, CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.5f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLastPage) {
                    TextButton(onClick = onSkip) {
                        Text("skip", color = Color.Gray)
                    }
                    
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF84A933)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text("Next")
                    }
                } else {
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF84A933)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("GET STARTED")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Onboarding 1")
@Composable
fun Onboarding1Preview() {
    TestingTheme {
        OnboardingScreen(
            title = "Find and located charging Stations Near you",
            subtitle = "Find available charging points easily and reserve your charging point",
            image = Icons.Default.Map,
            pageIndex = 0,
            onNext = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true, name = "Onboarding 2")
@Composable
fun Onboarding2Preview() {
    TestingTheme {
        OnboardingScreen(
            title = "Book and pay at any time from anywhere",
            subtitle = "Reserve your slot as per your choice and then pay online to confirm booking",
            image = Icons.Default.Payment,
            pageIndex = 1,
            onNext = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true, name = "Onboarding 3")
@Composable
fun Onboarding3Preview() {
    TestingTheme {
        OnboardingScreen(
            title = "Sit back and relax, let your EV charge",
            subtitle = "Reach the charging point per your slot and charge comfortably",
            image = Icons.Default.ElectricScooter,
            pageIndex = 2,
            onNext = {},
            onSkip = {},
            isLastPage = true
        )
    }
}
