package com.example.mindease.ui.theme

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke

import com.example.mindease.data.MoodEntry
import com.example.mindease.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.graphics.Path

import androidx.compose.foundation.Canvas


// Define a custom color scheme
private val customColors = lightColorScheme(
    primary = Color(0xFF6200EE), // Main primary color
    secondary = Color(0xFF03DAC6), // Secondary color
    background = Color(0xFFF9F9F9), // Background color
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun MoodTrackerScreen(
    moodViewModel: MoodViewModel = viewModel()
) {
    var mood by remember { mutableStateOf(5f) }
    var note by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Apply custom theme
    MaterialTheme(colorScheme = customColors) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(), // Removed verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("How are you feeling?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = mood,
                onValueChange = { mood = it },
                valueRange = 0f..10f,
                steps = 10,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display mood emoji based on value
            Text(
                text = when {
                    mood <= 3 -> "😞"
                    mood in 4f..6f -> "😐"
                    else -> "😊"
                },
                fontSize = 60.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Optional note") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = customColors.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isSubmitting = true
                    moodViewModel.saveMood(mood, note)
                    Toast.makeText(context, "Mood saved!", Toast.LENGTH_SHORT).show()
                    note = ""
                    isSubmitting = false
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = customColors.primary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Submit", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            RecentMoodsDisplay(moodViewModel)
        }
    }
}

@Composable
fun RecentMoodsDisplay(moodViewModel: MoodViewModel) {
    val recentMoods by moodViewModel.moods.observeAsState(emptyList())

    if (recentMoods.isNotEmpty()) {
        Text("Recent Moods", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Extract mood values for the chart (e.g., last 7 entries)
        val moodValues = recentMoods.takeLast(7).map { it.mood }

        MoodSparklineChart(moods = moodValues)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recentMoods) { moodEntry ->
                SwipeToDismissItem(moodEntry, moodViewModel)
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissItem(moodEntry: MoodEntry, moodViewModel: MoodViewModel) {
    val dismissState = rememberDismissState()

    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
        LaunchedEffect(Unit) {
            moodViewModel.deleteMood(moodEntry)
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        dismissThresholds = {
            FractionalThreshold(0.5f)
        },
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(customColors.secondary)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        },
        dismissContent = {
            MoodCard(moodEntry)
        }
    )
}

@Composable
fun MoodCard(moodEntry: MoodEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mood: ${moodEmojiLabel(moodEntry.mood)} (${moodEntry.mood})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Note: ${moodEntry.note ?: "No note provided"}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: ${formatTimestamp(moodEntry.date)}")
        }
    }
}
@Composable
fun MoodSparklineChart(moods: List<Float>, modifier: Modifier = Modifier) {
    if (moods.size < 2) return

    Canvas(modifier = modifier.height(120.dp).fillMaxWidth()) {
        val spacing = size.width / (moods.size - 1)
        val height = size.height
        val maxMood = 10f
        val minMood = 0f

        val points = moods.mapIndexed { index, mood ->
            val x = index * spacing
            val y = height - (mood - minMood) / (maxMood - minMood) * height
            Offset(x, y)
        }

        // Draw gradient fill
        val path = Path().apply {
            moveTo(points.first().x, height)
            points.forEach { point -> lineTo(point.x, point.y) }
            lineTo(points.last().x, height)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFBB86FC).copy(alpha = 0.4f), Color.Transparent),
                endY = height
            )
        )

        // Draw smooth line (curve)
        val linePath = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val current = points[i]
                val midPoint = Offset((prev.x + current.x) / 2, (prev.y + current.y) / 2)
                quadraticBezierTo(prev.x, prev.y, midPoint.x, midPoint.y)
            }
            lineTo(points.last().x, points.last().y)
        }

        drawPath(
            path = linePath,
            color = Color(0xFF6200EE),
            style = Stroke(width = 4f)
        )

        // Optional: draw dots at each mood point
        points.forEach {
            drawCircle(
                color = Color(0xFF6200EE),
                radius = 6f,
                center = it
            )
        }
    }
}


fun moodEmojiLabel(mood: Float): String {
    return when {
        mood <= 3 -> "😞 Sad"
        mood in 4f..6f -> "😐 Neutral"
        else -> "😊 Happy"
    }
}

fun formatMoodTimestamp(timestamp: Long?): String {
    return timestamp?.let {
        val date = Date(it)
        SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(date)
    } ?: "No date"
}