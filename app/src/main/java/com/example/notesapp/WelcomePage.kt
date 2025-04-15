package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityWelcomePageBinding

class WelcomePage : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome_page)
        binding = ActivityWelcomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttn_animation_login = AnimationUtils.loadAnimation(this, R.anim.button_press)
        val buttn_animation_signUp = AnimationUtils.loadAnimation(this, R.anim.button_press)


        binding.welcomePageSignUpButton.setOnClickListener {
            it.startAnimation(buttn_animation_signUp)
            it.postDelayed({
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }, 100)
        }

        binding.welcomePageloginButton.setOnClickListener {
            it.startAnimation(buttn_animation_login)
            it.postDelayed({
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            },100)
        }
    }
}