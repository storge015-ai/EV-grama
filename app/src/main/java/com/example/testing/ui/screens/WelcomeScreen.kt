package com.example.testing.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0FDF4),
                        Color(0xFFDCFCE7).copy(alpha = 0.5f),
                        Color.White
                    )
                )
            )
    ) {
        // Decorative floating leaves (simplified representations)
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = Color(0xFF10B981).copy(alpha = 0.1f),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 100.dp)
        )
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = Color(0xFF10B981).copy(alpha = 0.1f),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-10).dp, y = (-50).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Top Header
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF1F2937))) {
                        append("Powering Communities,\n")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF10B981))) {
                        append("Empowering Futures")
                    }
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Empowering rural India with sustainable\nmobility and micro-entrepreneurship.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Small green separator line
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(Color(0xFF10B981), RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Central Illustration Section
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main circle with glow effect
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .background(Color(0xFF10B981).copy(alpha = 0.03f), CircleShape)
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(180.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 12.dp,
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.1f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = Color(0xFF10B981)
                            )
                        }
                    }
                }

                // Satellite Icons surrounding the main circle
                SatelliteIcon(
                    icon = Icons.Default.Eco,
                    modifier = Modifier.align(Alignment.TopStart).offset(x = 30.dp, y = 30.dp)
                )
                SatelliteIcon(
                    icon = Icons.Default.BatteryChargingFull,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (-30).dp, y = 30.dp)
                )
                SatelliteIcon(
                    icon = Icons.Default.Groups,
                    modifier = Modifier.align(Alignment.BottomStart).offset(x = 30.dp, y = (-30).dp)
                )
                SatelliteIcon(
                    icon = Icons.Default.ElectricScooter,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-30).dp, y = (-30).dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Text
            Text(
                text = "EV-Grama",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2937)
                )
            )

            Text(
                text = "COMMUNITY CHARGING",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rural green mobility through\nmicro-entrepreneurship.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (i == 0) 10.dp else 8.dp)
                            .background(
                                if (i == 0) Color(0xFF10B981) else Color.LightGray.copy(alpha = 0.5f),
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Section: Bike Icon and Next Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricScooter,
                    contentDescription = "Bike",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(70.dp)
                )

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1f)
                        .padding(start = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NEXT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SatelliteIcon(icon: ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(onNext = {})
    }
}
