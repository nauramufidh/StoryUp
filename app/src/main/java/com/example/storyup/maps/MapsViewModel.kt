package com.example.storyup.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyup.UserRepository
import com.example.storyup.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _storyResponse = MutableStateFlow<StoryResponse?>(null)
    val storyResponse: StateFlow<StoryResponse?> = _storyResponse

    fun getToken(): Flow<String?> {
        return userRepository.getToken()
    }

    fun fetchStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getStoriesWithLocation(token)
                _storyResponse.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
