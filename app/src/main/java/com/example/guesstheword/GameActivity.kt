package com.example.guesstheword

import android.content.Intent
import android.icu.lang.UCharacter.toLowerCase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import kotlin.concurrent.thread
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GameActivity : AppCompatActivity() {
    private val answer: String = "ПОСОХ"
    private val maxSymbols: Int = 5
    private var userAnswer: String = ""
    private lateinit var gameField: GameField
    private lateinit var buttonCheck: Button
    private var correctSymbols: MutableMap<Int, Char> = mutableMapOf()
    private var keyboardButton: MutableMap<Char, Button> = mutableMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportActionBar?.hide()
        initializeButtonBack()
        initializeFields()
        initializeKeyboard()
        initializeButtonCheck()
    }
    private fun initializeButtonBack() {
        var button: ImageButton = findViewById<ImageButton>(R.id.buttonBack)

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    private fun initializeFields() {
        val field = findViewById<TableLayout>(R.id.fieldContainer)
        val firstRow = findViewById<TableRow>(R.id.row1)
        val secondRow = findViewById<TableRow>(R.id.row2)
        val thirdRow = findViewById<TableRow>(R.id.row3)
        val fourthRow = findViewById<TableRow>(R.id.row4)
        val fifthRow = findViewById<TableRow>(R.id.row5)
        val sixthRow = findViewById<TableRow>(R.id.row6)

        gameField = GameField(
            maxColumns = maxSymbols,
            gameField = field,
            maxRows = 6,
            context = this,
            firstRow = firstRow,
            secondRow = secondRow,
            thirdRow = thirdRow,
            fourthRow = fourthRow,
            fifthRow = fifthRow,
            sixthRow = sixthRow,
        )

    }
    private fun initializeKeyboard() {
        val keyButtonIds = arrayOf(
            R.id.key1_1, R.id.key1_2, R.id.key1_3, R.id.key1_4, R.id.key1_5, R.id.key1_6, R.id.key1_7, R.id.key1_8, R.id.key1_9,
            R.id.key1_10, R.id.key1_11, R.id.key1_12, R.id.key1_13, R.id.key2_1, R.id.key2_2, R.id.key2_3, R.id.key2_4, R.id.key2_5,
            R.id.key2_6, R.id.key2_7, R.id.key2_8, R.id.key2_9, R.id.key2_10, R.id.key2_11,
            R.id.key3_1, R.id.key3_2, R.id.key3_3,  R.id.key3_4, R.id.key3_5,R.id.key3_6,R.id.key3_7,R.id.key3_8,R.id.key3_9,
        )

        for (buttonId in keyButtonIds) {
            val keyButton = findViewById<Button>(buttonId)
            keyButton.setOnClickListener {
                val nextColumn = if (gameField.nowColumn != maxSymbols) gameField.nowColumn + 1 else gameField.nowColumn
                gameField.setText(gameField.nowRow, gameField.nowColumn, keyButton.text.toString(), nextColumn)
            }

            keyboardButton[keyButton.text[0]] = keyButton
        }

        val keyButtonBackspaceFrame = findViewById<FrameLayout>(R.id.key3_10Frame)
        val keyButtonBackspace = findViewById<ImageButton>(R.id.key3_10)

        keyButtonBackspaceFrame.setOnClickListener {
            val prevColumn = if (gameField.nowColumn - 1 != 0) gameField.nowColumn - 1 else gameField.nowColumn
            gameField.setText(gameField.nowRow, gameField.nowColumn, "", prevColumn)
        }

        keyButtonBackspace.setOnClickListener {
            val prevColumn = if (gameField.nowColumn - 1 != 0) gameField.nowColumn - 1 else gameField.nowColumn
            gameField.setText(gameField.nowRow, gameField.nowColumn, "", prevColumn)
        }

    }
    private fun initializeButtonCheck() {
        buttonCheck = findViewById(R.id.buttonCheckWord)

        buttonCheck.setOnClickListener {
            userAnswer = gameField.getWord(gameField.nowRow)

            thread {
                if (userAnswer.length < answer.length) {
                    // TODO: Реализовать всплывающее сообщение об ошибке
                    Log.d("Word", "Недостаточно букв")
                } else {
                    if (!isWordSpelledCorrectly(userAnswer)) {
                        // TODO: Реализовать всплывающее сообщение об ошибке, что такого слова нет в русском языке
                        Log.d("Word", "Такого слова нет в словаре")
                    }

                    else {
                        runOnUiThread {
                            setLine() // TODO: Реализация победы/поражения или переход на следующую строку
                        }
                    }
                }
            }
        }
    }
    private fun setLine() {
        if (gameField.nowRow == 6 || answer == userAnswer) {
            // TODO: Реализация победы и поражения
        }

        else {
            for (i in 1..5) {
                var isOk: Boolean = false
                if (userAnswer[i-1] == answer[i-1]) {
                    gameField.setStyleField(gameField.nowRow, i, FieldState.CORRECT)
                    correctSymbols.apply {
                        put(i, userAnswer[i-1])
                    }

                    setBackgroundKeyboard(keyboardButton[userAnswer[i-1]]!!, FieldState.CORRECT)
                }

                else if (answer.contains(userAnswer[i-1])) {
                    val count = answer.count { it == userAnswer[i-1]}
                    val index = answer.indexOf(userAnswer[i-1])

                    if (count == 1 && answer[index] == userAnswer[index]) {
                        gameField.setStyleField(gameField.nowRow, i, FieldState.INCORRECT)
                    }

                    else if (count == 1 && answer[index] != userAnswer[index]) {
                        gameField.setStyleField(gameField.nowRow, i, FieldState.INCORRECT)

                        for (j in 1..5) {
                            if (userAnswer[j-1] == userAnswer[i-1]) {
                                gameField.setStyleField(gameField.nowRow, j, FieldState.POSSIBLY)
                                setBackgroundKeyboard(keyboardButton[userAnswer[j-1]]!!, FieldState.POSSIBLY)
                                break
                            }
                        }
                    }
                    else {
                        gameField.setStyleField(gameField.nowRow, i, FieldState.POSSIBLY)
                        setBackgroundKeyboard(keyboardButton[userAnswer[i-1]]!!, FieldState.POSSIBLY)
                    }
                }

                else {
                    gameField.setStyleField(gameField.nowRow, i, FieldState.INCORRECT)
                    setBackgroundKeyboard(keyboardButton[userAnswer[i-1]]!!, FieldState.INCORRECT)
                }
            }
            gameField.setNumberLine(gameField.nowRow+1, correctSymbols)
        }
    }
    private fun isWordSpelledCorrectly(word: String): Boolean {
        var reqParam = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("query", "UTF-8")
        reqParam += "&" + URLEncoder.encode("format", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")
        reqParam += "&" + URLEncoder.encode("titles", "UTF-8") + "=" + URLEncoder.encode(toLowerCase(word), "UTF-8")
        val mURL = URL("https://ru.wiktionary.org/w/api.php?$reqParam")

        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()

                if (responseCode == 200) {
                    val json = JSONObject(response.toString())
                    val pages = json.getJSONObject("query").getJSONObject("pages")

                    for (key in pages.keys()) {
                        if (key != "-1") {
                            return true
                        }
                    }
                } else {
                    // Error occurred while making the request
                    Log.d("Error: ", "$responseCode")
                }
            }
        }

        return false
    }

    private fun setBackgroundKeyboard(value: Button, fieldState: FieldState) {
        val backgroundResId =
            when(fieldState) {
                FieldState.CORRECT -> R.drawable.keyboard_button_correct_background
                FieldState.INCORRECT -> R.drawable.keyboard_button_incorrect_background
                FieldState.POSSIBLY -> R.drawable.keyboard_button_possibly_background
                else -> R.drawable.keyboard_button_background
            }
        value.setBackgroundResource(backgroundResId)
    }
    private fun displayVictoryGame() {

    }

    private fun displayDefeatGame() {

    }
}