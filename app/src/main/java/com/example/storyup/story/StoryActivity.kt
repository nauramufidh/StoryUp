package com.example.storyup.story

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyup.MainActivity
import com.example.storyup.R
import com.example.storyup.UserPreferences
import com.example.storyup.UserRepository
import com.example.storyup.data.retrofit.ApiConfig
import com.example.storyup.dataStore
import com.example.storyup.databinding.ActivityStoryBinding
import com.example.storyup.maps.MapsActivity
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("EXTRA_STORY", story)
            startActivity(intent)
        }
    }

    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(
            UserRepository(
                ApiConfig.getApiService(),
                UserPreferences.getInstance(dataStore)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            adapter = storyAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        storyViewModel.stories.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }

        storyViewModel.isLoading.observe(this) { isLoading ->
            binding.progressbar3.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_story, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuLogout -> {
                logout()
                true
            }

            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            val userPreferences = UserPreferences.getInstance(dataStore)
            userPreferences.clearToken()

            val intent = Intent(this@StoryActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
