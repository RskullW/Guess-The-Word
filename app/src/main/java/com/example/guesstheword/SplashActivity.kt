package com.example.guesstheword

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.guesstheword.database.DataBase
import com.guesstheword.database.DataCallback
import com.guesstheword.stats.GameSettings
import com.guesstheword.text.Outline.GradientTextView

class SplashActivity : AppCompatActivity(), DataCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        GameSettings.loadStats(this)
        DataBase.fetchCategories(this)

        findViewById<GradientTextView>(R.id.splashGradientTextView).setGradientColors(Color.parseColor("#AA222BFF"), Color.parseColor("#AAA8EC52"))
    }

    override fun onDataLoaded() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }
}