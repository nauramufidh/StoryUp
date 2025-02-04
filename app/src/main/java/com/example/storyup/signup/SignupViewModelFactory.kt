package com.example.storyup.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyup.UserPreferences
import com.example.storyup.UserRepository
import com.example.storyup.data.retrofit.ApiConfig

class SignupViewModelFactory(private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userRepository = UserRepository(ApiConfig.getApiService(), userPreferences)
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
