package com.example.storyup.story

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyup.data.retrofit.ApiConfig
import com.example.storyup.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()

    fun uploadStory(
        token: String,
        file: File,
        description: String,
        onSuccess: (AddStoryResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val descriptionPart = RequestBody.create("text/plain".toMediaType(), description)
        val filePart = MultipartBody.Part.createFormData(
            "photo", file.name, file.asRequestBody("image/jpeg".toMediaType())
        )

        viewModelScope.launch {
            try {
                val response = apiService.uploadStory("Bearer $token", descriptionPart, filePart)
                Log.d("API Response", response.toString())
                onSuccess(response)
            } catch (e: Throwable) {
                Log.e("API Error", e.message, e)
                onError(e)
            }
        }
    }
}
