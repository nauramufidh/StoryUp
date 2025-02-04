package com.example.storyup.maps

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyup.R
import com.example.storyup.UserPreferences
import com.example.storyup.UserRepository
import com.example.storyup.data.retrofit.ApiConfig
import com.example.storyup.dataStore
import com.example.storyup.databinding.ActivityMapsBinding
import com.example.storyup.response.ListStoryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: MapsViewModel by viewModels {
        MapsViewModelFactory(
            UserRepository(
                ApiConfig.getApiService(),
                UserPreferences.getInstance(dataStore)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeToken()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("MapsActivity", "Map is ready")
    }

    private fun observeToken() {
        lifecycleScope.launch {
            viewModel.getToken().collect { token ->
                token?.let {
                    viewModel.fetchStoriesWithLocation("Bearer $it")
                }
            }
        }

        observeStories()
    }

    private fun observeStories() {
        lifecycleScope.launchWhenStarted {
            viewModel.storyResponse.collect { response ->
                response?.listStory?.let { stories ->
                    displayStoriesOnMap(stories)
                }
            }
        }
    }

    private fun displayStoriesOnMap(stories: List<ListStoryItem>) {
        if (::mMap.isInitialized) {
           mMap.clear()
            for (story in stories) {
                val lat = story.lat ?: continue
                val lon = story.lon ?: continue
                val location = LatLng(lat, lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(story.name)
                        .snippet(story.description)
                )
            }

            if (stories.isNotEmpty()) {
                val firstLocation = LatLng(stories[0].lat!!, stories[0].lon!!)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
            }
        }
    }
}
