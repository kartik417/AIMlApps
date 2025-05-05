package com.example.mindease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindease.data.JournalDatabase
import com.example.mindease.data.JournalViewModelFactory
import com.example.mindease.repository.JournalRepository
import com.example.mindease.ui.theme.*
import com.example.mindease.viewmodel.ChatViewModel
import com.example.mindease.viewmodel.JournalViewModel
import com.example.mindease.viewmodel.LoginRegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = JournalDatabase.getDatabase(applicationContext)
        val repository = JournalRepository(database.journalDao())
        val journalViewModelFactory = JournalViewModelFactory(repository)

        setContent {
            MindEaseTheme(dynamicColor = false) {
                var isSplashScreenVisible by remember { mutableStateOf(true) }
                val navController = rememberNavController()

                // Show Splash Screen for 3 seconds, then navigate to the appropriate screen
                if (isSplashScreenVisible) {
                    SplashScreen(onTimeout = {
                        isSplashScreenVisible = false
                    })
                } else {
                    // Check login status
                    val loginViewModel: LoginRegisterViewModel = viewModel()
                    val startDestination = if (loginViewModel.isUserLoggedIn()) "home" else "login"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login") {
                            LoginScreen(navController, loginViewModel)
                        }
                        composable("register") {
                            RegisterScreen(navController, loginViewModel)
                        }
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("mood") {
                            val chatViewModel: ChatViewModel = viewModel()
                            MoodTrackerScreen(viewModel = chatViewModel)
                        }

                        composable("breathing") {
                            BreathingScreen()
                        }
                        composable("journal") {
                            val journalViewModel: JournalViewModel = viewModel(factory = journalViewModelFactory)
                            val entries by journalViewModel.entries.collectAsState()

                            JournalScreen(
                                viewModel = journalViewModel
                            )
                        }

                        composable("chat") {
                            GPTChatScreen(viewModel())
                        }
                        composable("fullArticle/{title}") { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            FullArticleScreen(title = title)
                        }
                    }
                }
            }
        }
    }
}
