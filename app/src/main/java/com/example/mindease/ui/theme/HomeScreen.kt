package com.example.mindease.ui.theme

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import kotlinx.coroutines.delay



@Composable
fun HomeScreen(navController: NavHostController) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1E88E5), Color(0xFF42A5F5))
    )

    var quote by remember { mutableStateOf("Loading quote...") }
    var author by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getRandomQuote()
            quote = "“${response.content}”"
            author = "– ${response.author}"
        } catch (e: Exception) {
            quote = "Failed to load quote"
            author = ""
            Log.e("HomeScreen", "Error fetching quote: ${e.message}", e)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { TopBar() }

            item {
                ScrollAnimatedItem(index = 0) {
                    Text(
                        text = "Welcome to MindEase 👋",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            val features = listOf(
                Feature(Icons.Filled.EmojiEmotions, "Mood Tracker", "Log your emotions and monitor your mental well-being.", "mood"),
                Feature(Icons.Filled.SelfImprovement, "Breathing Exercises", "Relax with guided breathing to reduce stress.", "breathing"),
                Feature(Icons.Filled.MenuBook, "Daily Journal", "Pen down your thoughts and reflect daily.", "journal"),
                Feature(Icons.Filled.Chat, "AI Therapist", "Chat with an AI-powered therapist anytime.", "chat")
            )

            itemsIndexed(features) { index, feature ->
                ScrollAnimatedItem(index = index + 1) {
                    FeatureCard(
                        icon = feature.icon,
                        title = feature.title,
                        description = feature.description
                    ) {
                        navController.navigate(feature.route)
                    }
                }
            }

            // 💬 Quote of the Day from API
            item {
                ScrollAnimatedItem(index = features.size + 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            "Quote of the Day 🌟",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Yellow
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            quote,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        if (author.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                author,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontStyle = FontStyle.Italic
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "Explore Articles 📰",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            val articles = listOf(
                "Mindfulness Tips" to "Learn how to stay mindful throughout your day",
                "Productivity Hacks" to "Improve your productivity with these tips",
                "Digital Detox" to "Unplug and recharge with digital detox strategies",
                "Healthy Sleep" to "Improve your sleep quality and routine",
                "Positive Affirmations" to "Boost confidence with daily affirmations"
            )

            itemsIndexed(articles) { index, (title, summary) ->
                ScrollAnimatedItem(index = features.size + index + 2) {
                    ArticleCard(title = title, summary = summary) {
                        navController.navigate("fullArticle/$title")
                    }
                }
            }
        }
    }
}


// Animated scroll for each item
@Composable
fun ScrollAnimatedItem(index: Int, content: @Composable () -> Unit) {
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(index) {
        delay(index * 200L)  // Delay increased to stagger animations more smoothly
        visible.value = true
    }

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn()
    ) {
        content()
    }
}

data class Feature(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val route: String
)

@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp),
                tint = Color(0xFF0288D1)
            )
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF01579B)
                    )
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF01579B).copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun ArticleCard(title: String, summary: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    lineHeight = 20.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text("MindEase", color = Color.White, style = MaterialTheme.typography.titleLarge)
        },
        actions = {
            IconButton(onClick = { /* handle notification */ }) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
    )
}

@Composable
fun FullArticleScreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Here goes the full content of the article \"$title\"...")
    }
}

