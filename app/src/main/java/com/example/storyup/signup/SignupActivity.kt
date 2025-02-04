package com.example.storyup.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyup.UserPreferences
import com.example.storyup.dataStore
import com.example.storyup.databinding.ActivitySignupBinding
import com.example.storyup.login.LoginActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        val userPreferences = UserPreferences.getInstance(dataStore)
        SignupViewModelFactory(userPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        binding.progressbar1.visibility = android.view.View.GONE

        binding.btnSignup.setOnClickListener {
            val name = binding.tilNama.text.toString()
            val email = binding.tilEmail.text.toString()
            val password = binding.tilPassword.text.toString()

            when {
                name.isEmpty() -> {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() || password.length < 8 -> {
                    Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressbar1.visibility = android.view.View.VISIBLE
                    signupViewModel.register(name, email, password)
                }
            }
        }

        lifecycleScope.launch {
            signupViewModel.registerResult.collect { response ->
                binding.progressbar1.visibility = android.view.View.GONE
                response?.let {
                    if (it.error == true) {
                        Toast.makeText(this@SignupActivity, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignupActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.ivSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val msgSignup = ObjectAnimator.ofFloat(binding.msgSignup, View.ALPHA, 1f).setDuration(400)
        val name = ObjectAnimator.ofFloat(binding.nameSignup, View.ALPHA, 1f).setDuration(400)
        val email = ObjectAnimator.ofFloat(binding.emailSignup, View.ALPHA, 1f).setDuration(400)
        val passw = ObjectAnimator.ofFloat(binding.passwSignup, View.ALPHA, 1f).setDuration(400)
        val signup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(400)


        AnimatorSet().apply {
            playSequentially(msgSignup, name, email, passw, signup)
            start()
        }
    }
}
