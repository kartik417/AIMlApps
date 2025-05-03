package com.example.mindease.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindease.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // Splash screen stays for 3 seconds
        onTimeout() // Trigger the timeout to navigate
    }

    // Splash screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)), // Set the background color from the theme
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Image logo of the app (ensure it's placed in res/drawable folder)
            Image(
                painter = painterResource(id = R.drawable.mindease), // Replace with your logo image
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp) // Adjust the size of the logo
            )
            Spacer(modifier = Modifier.height(16.dp)) // Space between logo and text
            Text(
                text = "MindEase",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE) // Primary color for the text
            )
        }
    }
}
