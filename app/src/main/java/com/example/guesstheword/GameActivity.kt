package com.example.guesstheword

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class GameActivity : AppCompatActivity() {
    private val answer: String = "ОБЫСК"
    private val maxSymbols: Int = 5
    private var userAnswer: String = ""
    private lateinit var gameField: GameField

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportActionBar?.hide()
        initializeButtonBack()
        initializeFields()
        initializeKeyboard()
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
}