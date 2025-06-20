package com.mohsin.chatly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.mohsin.chatly.R
import com.mohsin.chatly.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest


class RegisterActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val displayName = findViewById<TextInputEditText>(R.id.etName)
        val mobile = findViewById<TextInputEditText>(R.id.etMobile)
        val email = findViewById<TextInputEditText>(R.id.etEmail)
        val pass = findViewById<TextInputEditText>(R.id.etPassword)

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            viewModel.register(
                displayName.text.toString(),
                mobile.text.toString(),
                email.text.toString(),
                pass.text.toString()
            )
        }

        findViewById<TextView>(R.id.tvToLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.authState.collectLatest { if (it) navigateMain() }
            viewModel.error.collectLatest { it?.let { Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show() } }
        }
    }

    private fun navigateMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
