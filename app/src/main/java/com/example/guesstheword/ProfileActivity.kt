package com.example.guesstheword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.guesstheword.stats.GameSettings
import com.guesstheword.text.Outline.OutlineTextView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()

        initializeButtons()
        initializeText()
    }

    private fun initializeButtons() {
        findViewById<ImageButton>(R.id.buttonBackMenuFromProfile).setOnClickListener {
            finish()
        }

        findViewById<FrameLayout>(R.id.buttonResetStats).setOnClickListener {
            GameSettings.resetStats(this)
            Log.d("ProfileActivity", "reset stats")
            Toast.makeText(this, "Статистика сброшена", Toast.LENGTH_LONG).show()
            finish()
        }

        findViewById<FrameLayout>(R.id.buttonPlayTelegram).setOnClickListener {

        }
    }
    private fun initializeText() {
        findViewById<OutlineTextView>(R.id.victoryOutlineTextView).text = "Слов угадано: ${GameSettings.victoryRounds}"
        findViewById<OutlineTextView>(R.id.defeatOutlineTextView).text = "Поражений: ${GameSettings.defeatRounds}"
        findViewById<OutlineTextView>(R.id.allRoundsOutlineTextView).text = "Игр сыграно: ${GameSettings.victoryRounds + GameSettings.defeatRounds}"


        val favoriteCategory = GameSettings.categoriesPlayed.maxByOrNull { it.value }?.key

        if (favoriteCategory == null) {
            findViewById<OutlineTextView>(R.id.likeCategoryOutlineTextView).text = "Любимая категория: нет"
        }

        else {
            findViewById<OutlineTextView>(R.id.likeCategoryOutlineTextView).text = "Любимая категория: $favoriteCategory"
        }
    }
}