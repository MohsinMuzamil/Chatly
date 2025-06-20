package com.mohsin.chatly.ui.splash
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mohsin.chatly.R
import com.mohsin.chatly.ui.main.GreetingActivity
import com.mohsin.chatly.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Animate logo and text
        val logo = findViewById<ImageView>(R.id.ivLogo)
        val appName = findViewById<TextView>(R.id.tvAppName)
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_logo_fadein))
        appName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_text_slideup))

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is logged in, go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in, go to GreetingActivity
                startActivity(Intent(this, GreetingActivity::class.java))
            }
            finish()
        }, 2000) // 2 seconds delay
    }
}