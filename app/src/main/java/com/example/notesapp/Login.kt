package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val loginButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press)


        binding.loginButton.setOnClickListener {
            binding.LogINprogressBar?.visibility = View.VISIBLE
            it.startAnimation(loginButtonAnimation)
            it.postDelayed({
                val email = binding.loginemailInput.text.toString()
                val password = binding.loginpasswordInput.text.toString()


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    binding.LogINprogressBar?.visibility = View.GONE
                    Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show()
                    return@postDelayed
                }
                logIN(email, password)
            }, 100)

        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun logIN(email:String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    binding.LogINprogressBar?.visibility = View.GONE
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    updateUI(user)
                }
                else{
                    binding.LogINprogressBar?.visibility = View.GONE
                    Toast.makeText(this, "Invalid credentials, please try again", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }
    }

}

