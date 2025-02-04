package com.example.storyup.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyup.UserRepository
import com.example.storyup.response.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableStateFlow<RegisterResponse?>(null)
    val registerResult: StateFlow<RegisterResponse?> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.registerUser(name, email, password)
                _registerResult.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
