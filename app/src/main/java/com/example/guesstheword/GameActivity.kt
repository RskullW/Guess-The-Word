package com.example.guesstheword

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow

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

        val gameField = GameField(
            maxColumns = maxSymbols,
            gameField = field,
            maxRows = 5,
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
        // TODO: initialize every key + logic key
    }
}