package com.example.mahirexpress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.User
import com.example.mahirexpress.util.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isSuccess = MutableLiveData<Boolean>(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun loginUser(email: String, password: String, preferenceManager: PreferenceManager) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter email and password"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

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
                            _isSuccess.value = true
                        } else {
                             _errorMessage.value = "User data not found"
                        }
                        _isLoading.value = false
                    }.addOnFailureListener {
                        _errorMessage.value = "Failed to fetch user profile"
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = task.exception?.message
                }
            }
    }
}
