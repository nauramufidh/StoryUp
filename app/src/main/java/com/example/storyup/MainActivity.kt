package com.example.storyup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyup.databinding.ActivityMainBinding
import com.example.storyup.login.LoginActivity
import com.example.storyup.signup.SignupActivity
import com.example.storyup.story.StoryActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserSession()
        playAnimation()

        val btnSignup = findViewById<Button>(R.id.btn_signupMain)
        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val btnLogin = findViewById<Button>(R.id.btn_loginMain)
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserSession(){
        val userPreferences = UserPreferences.getInstance(dataStore)

        lifecycleScope.launch {
            userPreferences.getToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val intent = Intent(this@MainActivity, StoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.ivMain, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(400)
        val welmsg = ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(400)
        val signup = ObjectAnimator.ofFloat(binding.btnSignupMain, View.ALPHA, 1f).setDuration(400)
        val login = ObjectAnimator.ofFloat(binding.btnLoginMain, View.ALPHA, 1f).setDuration(400)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(welcome, welmsg, together)
            start()
        }
    }
}