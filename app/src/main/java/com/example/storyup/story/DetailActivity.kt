package com.example.storyup.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyup.databinding.ActivityDetailBinding
import com.example.storyup.response.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>("EXTRA_STORY")
        if (story != null) {
            displayStoryDetails(story)
        } else {
            finish()
        }
    }

    private fun displayStoryDetails(story: ListStoryItem) {
        binding.apply {
            tvDetail1.text = story.name ?: "Nama tidak tersedia"
            tvDetail2.text = story.description ?: "Deskripsi tidak tersedia"

            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(imageDetail)
        }
    }
}
