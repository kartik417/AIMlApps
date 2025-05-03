package com.example.mindease.ui.theme

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.mindease.R

@Composable
fun BreathingScreen(context: Context = LocalContext.current) {
    val radius = remember { Animatable(60f) }
    var isInhale by remember { mutableStateOf(true) }

    var inputTime by remember { mutableStateOf("60") }
    var remainingTime by remember { mutableStateOf(60) }

    var isRunning by remember { mutableStateOf(false) }

    // MediaPlayer state (store real instance)
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.ambient_music).apply {
            isLooping = true
        }
    }

    // Clean up MediaPlayer when screen is removed
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    // Start music only when running
    LaunchedEffect(isRunning) {
        if (isRunning && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        } else if (!isRunning && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }

    // Breathing animation
    LaunchedEffect(isRunning) {
        while (isRunning && remainingTime > 0) {
            radius.animateTo(
                targetValue = if (isInhale) 140f else 60f,
                animationSpec = tween(durationMillis = 4000)
            )
            isInhale = !isInhale
            delay(4000)
        }
    }

    // Countdown Timer
    LaunchedEffect(isRunning) {
        while (isRunning && remainingTime > 0) {
            delay(1000)
            remainingTime--
        }
        if (remainingTime == 0) isRunning = false
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Canvas(modifier = Modifier.size(300.dp)) {
            drawCircle(
                color = Color(0xFF88C0D0),
                radius = radius.value,
                center = Offset(size.width / 2, size.height / 2)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when {
                !isRunning && remainingTime == 0 -> "Session Complete"
                isInhale -> "Inhale..."
                else -> "Exhale..."
            },
            fontSize = 24.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Time Left: ${remainingTime}s",
            fontSize = 18.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = inputTime,
            onValueChange = { inputTime = it.filter { it.isDigit() } },
            label = { Text("Set Timer (in seconds)") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = {
                    val time = inputTime.toIntOrNull() ?: 60
                    remainingTime = time
                    isRunning = true
                },
                enabled = !isRunning
            ) {
                Text("Start")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                isRunning = false
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
            }) {
                Text("Stop")
            }
        }
    }
}

