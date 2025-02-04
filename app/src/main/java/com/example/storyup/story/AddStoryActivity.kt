package com.example.storyup.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyup.UserPreferences
import com.example.storyup.compressImage
import com.example.storyup.dataStore
import com.example.storyup.databinding.ActivityAddStoryBinding
import com.example.storyup.getFileFromUri
import com.example.storyup.getImageUri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddStoryViewModel::class)

        val userPreferences = UserPreferences.getInstance(dataStore)
        lifecycleScope.launch {
            token = userPreferences.getToken().first() ?: ""
        }

        binding.galleryAdd.setOnClickListener { startGallery() }
        binding.cameraAdd.setOnClickListener { startCamera() }
        binding.uploadAdd.setOnClickListener { uploadStory() }
    }

    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera(){
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun uploadStory(){
        val description = binding.etAdd.text.toString().trim()
        if (currentImageUri != null && description.isNotBlank()) {
            val file = getFileFromUri(this, currentImageUri!!)
            val compressedFile = compressImage(file)
            viewModel.uploadStory(
                token = token,
                file = compressedFile,
                description = description,
                onSuccess = {
                    Toast.makeText(this, "Story uploaded successfully!", Toast.LENGTH_SHORT).show()
                    finish()

                    val intent = Intent(this, StoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                },
                onError = { e ->
                    Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this, "Please provide a photo and description!", Toast.LENGTH_SHORT).show()
        }

    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {uri: Uri? ->
        if (uri != null){
            currentImageUri =uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera =registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage(){
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAdd.setImageURI(it)
        }
    }
}