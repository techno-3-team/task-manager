package com.techno_3_team.task_manager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.techno_3_team.task_manager.databinding.ActivityLoginBinding

class StartActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        val buttonWithoutAuth = loginBinding.continueWithoutAutorization
        val buttonGoogleAuth = loginBinding.continueWithGoogle

        buttonWithoutAuth.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonGoogleAuth.setOnClickListener {  }
    }
}