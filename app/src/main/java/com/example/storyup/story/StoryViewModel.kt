package com.example.storyup.story

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyup.UserRepository
import com.example.storyup.response.ListStoryItem
import kotlinx.coroutines.flow.onStart

class StoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val stories: LiveData<PagingData<ListStoryItem>> =
        userRepository.getStories()
            .onStart { _isLoading.postValue(true) }
            .cachedIn(viewModelScope)
            .asLiveData()
            .also {
                it.observeForever { _isLoading.postValue(false) }
            }
}

class StoryViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
