package com.example.guesstheword

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageButton
import android.widget.TextView

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        supportActionBar?.hide()

        initializeText()

        findViewById<ImageButton>(R.id.buttonBackMenu).setOnClickListener {
            finish()
        }
    }

    private fun initializeText() {
        var rulesTextView2 = findViewById<TextView>(R.id.rulesTextView2)
        val text = "После нажатия кнопки 'Проверить слово' каждая буква введенного слова будет выделена цветом:\nСерый цвет: буква отсутствует в загаданном слове.\nЖелтый цвет: буква есть в загаданном слове, но находится на другой позиции.\nЗеленый цвет: буква угадана и находится в правильной позиции.\nЕсли игрок угадывает загаданное слово до истечения 5 попыток, он побеждает. В противном случае, он проигрывает."
        val spannableString = SpannableString(text)


        var startIndex = text.indexOf("Серый цвет")
        var endIndex = startIndex + "Серый цвет".length
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#444444")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        startIndex = text.indexOf("Желтый цвет")
        endIndex = startIndex + "Желтый цвет".length
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FFD600")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        startIndex = text.indexOf("Зеленый цвет")
        endIndex = startIndex + "Зеленый цвет".length
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#62FF71")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        rulesTextView2.text = spannableString
    }
}