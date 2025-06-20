package com.mohsin.chatly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mohsin.chatly.R
import com.mohsin.chatly.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView


class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<TextInputEditText>(R.id.etEmail)
        val pass  = findViewById<TextInputEditText>(R.id.etPassword)
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            viewModel.login(email.text.toString(), pass.text.toString())
        }
        findViewById<TextView>(R.id.tvToRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.authState.collectLatest { if (it) navigateMain() }
            viewModel.error.collectLatest { it?.let { Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show() } }
        }
    }



    private fun navigateMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
