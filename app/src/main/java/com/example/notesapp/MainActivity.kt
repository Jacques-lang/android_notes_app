package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.database.NoteDatabase
import com.example.notesapp.repository.Repository
import com.example.notesapp.viewmodel.NoteViewModel
import com.example.notesapp.viewmodel.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViewModel()

        val signOut = findViewById<TextView>(R.id.signOut)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerlayout)

        val usernameID = findViewById<TextView>(R.id.username)

        val menuIcon_animation = AnimationUtils.loadAnimation(this, R.anim.button_press)



        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        val username = email?.substringBefore("@")
        usernameID.text = username


        signOut.setOnClickListener {
            it.startAnimation(menuIcon_animation)
            it.postDelayed({
                Firebase.auth.signOut()
                val intent = Intent(this, WelcomePage::class.java)
                startActivity(intent)
                finish()
            },100)
        }

    }

    private fun setUpViewModel(){
        val noteRepository = Repository(NoteDatabase(this))
        val viewModelFactory = ViewModelFactory(application, noteRepository)
        noteViewModel = ViewModelProvider(this, viewModelFactory)[NoteViewModel::class.java]
    }

}

