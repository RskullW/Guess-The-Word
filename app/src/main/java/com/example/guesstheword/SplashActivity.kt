package com.example.guesstheword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.guesstheword.database.DataBase
import com.guesstheword.database.DataCallback

class SplashActivity : AppCompatActivity(), DataCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        DataBase.fetchCategories(this)
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