package com.example.guesstheword

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import com.guesstheword.stats.GameSettings
import com.guesstheword.text.Outline.OutlineTextViewWhite

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
            val telegramLink = "http://t.me/wordleongolangbot"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(telegramLink)

            startActivity(intent)
        }

    }
    private fun initializeText() {
        findViewById<OutlineTextViewWhite>(R.id.victoryOutlineTextView).text = "Слов угадано: ${GameSettings.victoryRounds}"
        findViewById<OutlineTextViewWhite>(R.id.defeatOutlineTextView).text = "Поражений: ${GameSettings.defeatRounds}"
        findViewById<OutlineTextViewWhite>(R.id.allRoundsOutlineTextView).text = "Игр сыграно: ${GameSettings.victoryRounds + GameSettings.defeatRounds}"

        val favoriteCategory = GameSettings.categoriesPlayed.maxByOrNull { it.value }?.key

        if (favoriteCategory == null) {
            findViewById<OutlineTextViewWhite>(R.id.likeCategoryOutlineTextView).text = "Любимая категория: нет"
        }

        else {
            findViewById<OutlineTextViewWhite>(R.id.likeCategoryOutlineTextView).text = "Любимая категория: $favoriteCategory"
        }
    }
}