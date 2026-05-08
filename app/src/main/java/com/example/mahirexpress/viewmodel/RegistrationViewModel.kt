package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var role by mutableStateOf("Customer")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun registerUser() {
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }

        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }

        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val user = User(userId, name, email, phone, role)
                    
                    database.child(userId).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            isLoading = false
                            if (dbTask.isSuccessful) {
                                isSuccess = true
                            } else {
                                errorMessage = dbTask.exception?.message
                            }
                        }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message
                }
            }
    }
}
