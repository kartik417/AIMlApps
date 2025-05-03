package com.example.mindease.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mindease.data.AuthUiState
import com.google.firebase.auth.FirebaseAuth

class LoginRegisterViewModel : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        uiState = uiState.copy(isLoading = true)
        auth.signInWithEmailAndPassword(uiState.email, uiState.password)
            .addOnCompleteListener { task ->
                uiState = uiState.copy(isLoading = false)
                if (task.isSuccessful) onSuccess()
                else onError(task.exception?.localizedMessage ?: "Login failed")
            }
    }

    fun register(onSuccess: () -> Unit, onError: (String) -> Unit) {
        uiState = uiState.copy(isLoading = true)
        auth.createUserWithEmailAndPassword(uiState.email, uiState.password)
            .addOnCompleteListener { task ->
                uiState = uiState.copy(isLoading = false)
                if (task.isSuccessful) onSuccess()
                else onError(task.exception?.localizedMessage ?: "Registration failed")
            }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
    }
}
