package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = com.google.firebase.Firebase.auth

        val loginButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press)


        binding.SignUpButton.setOnClickListener {
            binding.SignUPprogressBar?.visibility = View.VISIBLE
            it.startAnimation(loginButtonAnimation)
            it.postDelayed({
                val email = binding.SignUpemailInput.text.toString()
                val password = binding.SignUppasswordInput.text.toString()


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    binding.SignUPprogressBar?.visibility = View.GONE
                    Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show()
                    return@postDelayed
                }
                createAccount(email, password)
            }, 100)
        }
    }

    private fun createAccount(email:String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.SignUPprogressBar?.visibility = View.GONE
                    Toast.makeText(this, "Email successfully created", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        binding.SignUPprogressBar?.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "This email is already associated with an account, please log in",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        binding.SignUPprogressBar?.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Invalid credentials, please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

