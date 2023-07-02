package com.example.guesstheword

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var buttonRandomWord: Button
    private var isButtonPressed: Boolean = false
    private val colorTextButtonPressed: Int = R.color.colorTextButtonPressed
    private val colorTextButtonNotPressed: Int = R.color.colorTextButtonNotPressed
    private var mapButtonMode: MutableMap<String, ButtonState>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initializeButtonsMode()
    }

    private fun initializeButtonsMode() {
        mapButtonMode = mutableMapOf()

        val buttonRandomWord = findViewById<Button>(R.id.buttonRandomWord)
        val buttonRandomWord2 = findViewById<Button>(R.id.buttonRandomWord2)

        val buttonState1 = ButtonState(button = buttonRandomWord, _isPressed = false)
        val buttonState2 = ButtonState(button = buttonRandomWord2, _isPressed = false)

        mapButtonMode?.apply {
            put("allwords", buttonState1)
            put("5word", buttonState2)
        }

        for (buttonState in mapButtonMode!!) {
            buttonState.value.button.setOnClickListener {
                var value: Boolean = buttonState.value.isPressed
                buttonState.value.isPressed = !value

                for (button in mapButtonMode!!) {
                    if (button == buttonState) continue
                    button.value.isPressed = false
                }
            }
        }
    }
}