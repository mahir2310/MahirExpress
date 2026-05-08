package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.User
import com.example.mahirexpress.util.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun loginUser(preferenceManager: PreferenceManager) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    
                    // Fetch user details from Database to save in Preferences
                    database.child(userId).get().addOnSuccessListener { snapshot ->
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            preferenceManager.saveData(
                                userId = user.userId,
                                name = user.name,
                                email = user.email,
                                role = user.role
                            )
                            isSuccess = true
                        }
                        isLoading = false
                    }.addOnFailureListener {
                        errorMessage = "Failed to fetch user profile"
                        isLoading = false
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message
                }
            }
    }
}
