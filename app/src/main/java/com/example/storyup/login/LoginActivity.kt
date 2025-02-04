package com.example.storyup.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyup.UserPreferences
import com.example.storyup.UserRepository
import com.example.storyup.data.retrofit.ApiConfig
import com.example.storyup.dataStore
import com.example.storyup.databinding.ActivityLoginBinding
import com.example.storyup.story.StoryActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        val userPreferences = UserPreferences.getInstance(dataStore)
        val userRepository = UserRepository(ApiConfig.getApiService(), userPreferences)
        loginViewModel = LoginViewModel(userRepository)

        setupView()
        observeViewModel()
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val loginMsg1 = ObjectAnimator.ofFloat(binding.tvLogin1, View.ALPHA, 1f).setDuration(400)
        val loginMsg2 = ObjectAnimator.ofFloat(binding.tvLogin2, View.ALPHA, 1f).setDuration(400)
        val email = ObjectAnimator.ofFloat(binding.punyaemail, View.ALPHA, 1f).setDuration(400)
        val password = ObjectAnimator.ofFloat(binding.punyapassw, View.ALPHA, 1f).setDuration(400)
        val btnmasuk = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(loginMsg1, loginMsg2, email, password, btnmasuk)
            start()
        }
    }

    private fun setupView() {
        binding.progressbar2.visibility = android.view.View.GONE

        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmailLogin.text.toString().trim()
            val password = binding.tilPasswordLogin.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilEmailLogin.error = "Email is required"
            } else {
                binding.tilEmailLogin.error = null
            }

            if (password.isEmpty()) {
                binding.tilPasswordLogin.error = "Password is required"
            } else {
                binding.tilPasswordLogin.error = null
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressbar2.visibility = android.view.View.VISIBLE
                loginViewModel.login(email, password)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginResult.collect { response ->
                binding.progressbar2.visibility = android.view.View.GONE
                response?.let {
                    if (it.error == true) {
                        Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
                    } else {
                        loginViewModel.fetchToken()
                    }
                }
            }
        }

        lifecycleScope.launch {
            loginViewModel.userToken.collect { token ->
                if (!token.isNullOrEmpty()) {

                    startActivity(Intent(this@LoginActivity, StoryActivity::class.java))
                    finish()
                }
            }
        }
    }
}

