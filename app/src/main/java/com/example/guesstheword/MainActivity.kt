package com.example.guesstheword

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var buttonRandomWord: Button
    private var isButtonPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        buttonRandomWord = findViewById<Button>(R.id.buttonRandomWord)
        buttonRandomWord.setOnClickListener {
            isButtonPressed = !isButtonPressed
            updateButtonColor()
        }
    }

    private fun updateButtonColor() {
        if (isButtonPressed) {
            buttonRandomWord.isActivated = false
        } else {
            buttonRandomWord.isActivated = true
        }
    }
}