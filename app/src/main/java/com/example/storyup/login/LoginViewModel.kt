package com.example.storyup.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyup.UserRepository
import com.example.storyup.response.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResponse?>(null)
    val loginResult: StateFlow<LoginResponse?> = _loginResult

    private val _isLoggedIn = MutableStateFlow(false)
    private val _userToken = MutableStateFlow<String?>(null)
    val userToken: StateFlow<String?> = _userToken

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.loginUser(email, password)
                _loginResult.value = response
                response.loginResult?.token?.let {

                    _isLoggedIn.value = true
                    fetchToken()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginResult.value = LoginResponse(error = true, message = "Login failed: ${e.message}")
            }
        }
    }

    fun fetchToken() {
        viewModelScope.launch {
            userRepository.getToken().collectLatest { token ->
                _userToken.value = token
            }
        }
    }
}