package com.example.storyup

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyup.data.retrofit.ApiService
import com.example.storyup.data.retrofit.StoryPagingSource
import com.example.storyup.response.ListStoryItem
import com.example.storyup.response.LoginResponse
import com.example.storyup.response.RegisterResponse
import com.example.storyup.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class UserRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {
    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun loginUser(email: String, password: String): LoginResponse{
        val response = apiService.login(email, password)
        response.loginResult?.token?.let { token ->
            userPreferences.saveToken(token)
        }
        return response
    }

    fun getStories(): Flow<PagingData<ListStoryItem>> {
        return userPreferences.getToken()
            .flatMapLatest { token ->
                Pager(
                    config = PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        StoryPagingSource(apiService, token.orEmpty())
                    }
                ).flow
            }
    }


    suspend fun getStoriesWithLocation(token: String): StoryResponse{
        return apiService.getStoriesWithLocation(token)
    }

    fun getToken(): Flow<String?> {
        return userPreferences.getToken()
    }
}