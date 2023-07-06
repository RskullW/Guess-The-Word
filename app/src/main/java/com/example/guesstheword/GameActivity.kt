package com.example.guesstheword

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.icu.lang.UCharacter.toLowerCase
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import com.anim.toast.CustomToast
import com.guesstheword.stats.GameSettings
import com.guesstheword.text.Outline.GradientTextView
import com.guesstheword.text.Outline.OutlineTextView
import com.guesstheword.timer.CustomTimer
import kotlin.concurrent.thread
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Duration

class GameActivity : AppCompatActivity() {
    private var answer: String = "ПОСОХ"
    private var maxSymbols: Int = 5
    private var userAnswer: String = ""
    private lateinit var gameField: GameField
    private lateinit var buttonCheck: Button
    private var correctSymbols: MutableMap<Int, Char> = mutableMapOf()
    private var keyboardButton: MutableMap<Char, Button> = mutableMapOf()
    private var isThreadRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportActionBar?.hide()
        createWord()
        initializeButtonBack()
        initializeFields()
        initializeKeyboard()
        initializeButtonCheck()
        initializeButtonFinish()
        setTextRightAnswer()

        CustomTimer.start()
    }

    private fun createWord() {
        Log.d("GAME_MODE", "${GameSettings.gameMode}")
        Log.d("CATEGORY", "${GameSettings.categoryName}")
        Log.d("WORD", "${GameSettings.word}")
        answer = if (GameSettings.word.length < 5) "ПОСОХ" else GameSettings.word
        maxSymbols = answer.length
    }
    private fun initializeButtonFinish() {
        var buttonExitMenu = findViewById<Button>(R.id.exitMenuButton)
        var buttonContinue = findViewById<Button>(R.id.continueButton)
        var buttonBack = findViewById<ImageButton>(R.id.buttonBack)

        buttonExitMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        buttonContinue.setOnClickListener {
            GameSettings.findWord()
            val intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
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
            isFinish = false,
            textSize = resources.getDimensionPixelSize(R.dimen.text_size_large6).toFloat(),
        )
    }
    private fun initializeKeyboard() {
        val keyButtonIds = arrayOf(
            R.id.key1_1, R.id.key1_2, R.id.key1_3, R.id.key1_4, R.id.key1_5, R.id.key1_6, R.id.key1_7, R.id.key1_8, R.id.key1_9,
            R.id.key1_10, R.id.key1_11, R.id.key1_12, R.id.key2_1, R.id.key2_2, R.id.key2_3, R.id.key2_4, R.id.key2_5,
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

        keyButtonBackspaceFrame.setOnLongClickListener {
            for (i in 1..6)  {
                gameField.setText(gameField.nowRow, i, "", 1)
            }
            true
        }

        keyButtonBackspace.setOnLongClickListener {
            keyButtonBackspaceFrame.performLongClick()
            true
        }

        keyButtonBackspace.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                keyButtonBackspaceFrame.isPressed = false
            }

            if (event.action == MotionEvent.ACTION_DOWN)
            {
                keyButtonBackspaceFrame.isPressed = true
                keyButtonBackspace.performClick()
            }
            false
        }

    }
    private fun initializeButtonCheck() {
        buttonCheck = findViewById(R.id.buttonCheckWord)

        buttonCheck.setOnClickListener {
            if (!isThreadRunning) {
                isThreadRunning = true

                userAnswer = gameField.getWord(gameField.nowRow)

                thread {
                    if (userAnswer.length < answer.length) {
                        runOnUiThread {
                            showCustomToast(
                                "Не хватает букв! Должно быть: ${answer.length} букв!"
                            )
                            isThreadRunning = false

                        }
                    } else {
                        if (!isWordSpelledCorrectly(userAnswer) && userAnswer != answer) {
                            runOnUiThread {
                                showCustomToast(
                                    "Такого слова нет в нашем словаре!\nПожалуйста, введите существительное в ед.числе, им.падеже. Например, ОБЫСК"
                                )
                                isThreadRunning = false
                            }
                        } else {
                            runOnUiThread {
                                setEnabledButtons(false)
                                setLine()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveStats(isVictory: Boolean) {
        if (isVictory) {
            GameSettings.victoryRounds++
        }

        else {
            GameSettings.defeatRounds++
        }

        if (GameSettings.nowCategory == "all") {
            GameSettings.categoriesPlayed["нет"] = GameSettings.categoriesPlayed.getOrDefault("нет", 0U) + 1U
        } else {
            GameSettings.categoriesPlayed[GameSettings.nowCategory] = GameSettings.categoriesPlayed.getOrDefault(GameSettings.nowCategory, 0U) + 1U
        }

        GameSettings.saveStats(this)
    }
    private fun setLine() {
        if (answer == userAnswer) {
            setEnabledButtons(false)

            for (column in 1..maxSymbols) {
                gameField.setStyleField(gameField.nowRow, column, FieldState.CORRECT)
            }

            Handler().postDelayed({
                displayStatsGame(true)
            }, 800)
        }
        else {
            setEnabledButtons(false)
            for (i in 1..maxSymbols) {
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

                        for (j in 1..maxSymbols) {
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

            if (gameField.nowRow == 6) {
                setEnabledButtons(false)
                Handler().postDelayed({
                    displayStatsGame(false)
                }, 800)
            }

            else {
                setEnabledButtons(true)
                isThreadRunning = false
                gameField.setNumberLine(gameField.nowRow + 1, correctSymbols)
            }
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
                    Log.d("Error: ", "$responseCode")
                }
            }
        }

        return false
    }
    private fun setBackgroundKeyboard(value: Button, fieldState: FieldState) {
        for (correctSymbol in correctSymbols) {
            if (value.text.contains(correctSymbol.value) && fieldState != FieldState.CORRECT) {
                return
            }
        }

        val backgroundResId =
            when(fieldState) {
                FieldState.CORRECT -> R.drawable.keyboard_button_correct_background
                FieldState.INCORRECT -> R.drawable.keyboard_button_incorrect_background
                FieldState.POSSIBLY -> R.drawable.keyboard_button_possibly_background
                else -> R.drawable.keyboard_button_background
            }
        value.setBackgroundResource(backgroundResId)
    }
    private fun displayStatsGame(isVictory: Boolean) {

        var finishFrameLayout = findViewById<FrameLayout>(R.id.finishFrameLayout)
        finishFrameLayout.visibility = View.VISIBLE

        setTextForFinishGame(isVictory)

        var field: GameField = getFieldForFinishWindow()
        saveStats(isVictory)
        for (row in 1..gameField.nowRow) {
            for (column in 1..maxSymbols) {
                field.setStyleField(row, column, gameField.getBackgroundStyle(row, column)!!)
            }
        }

        setTextTimer()
    }
    private fun setTextTimer() {
        var timerTextView = findViewById<OutlineTextView>(R.id.timerText)

        timerTextView.text = CustomTimer.getTimerToString()
        CustomTimer.stop()
    }
    private fun getFieldForFinishWindow(): GameField {
            val field = findViewById<TableLayout>(R.id.fieldContainerFinish)
            val firstRow = findViewById<TableRow>(R.id.finishRow1)
            val secondRow = findViewById<TableRow>(R.id.finishRow2)
            val thirdRow = findViewById<TableRow>(R.id.finishRow3)
            val fourthRow = findViewById<TableRow>(R.id.finishRow4)
            val fifthRow = findViewById<TableRow>(R.id.finishRow5)
            val sixthRow = findViewById<TableRow>(R.id.finishRow6)

            var fieldTemp = GameField(
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
                isFinish = true,
                textSize = resources.getDimensionPixelSize(R.dimen.text_size_large6).toFloat(),
                )

        if (gameField.nowRow != 6) {
            fieldTemp.setVisibleField(gameField.nowRow+1, 6, false)
        }
        return fieldTemp
    }
    private fun setTextForFinishGame(isVictory: Boolean) {
        val gradientTextView = findViewById<GradientTextView>(R.id.statusGameTextView)
        gradientTextView.setGradientColors(
            if (isVictory) Color.parseColor("#8EFF00") else Color.parseColor("#FF0000"),
            if (isVictory) Color.parseColor("#D6FFA1") else Color.parseColor("#FFA1A1")
        )

        gradientTextView.text = if (isVictory) "ПОБЕДА" else "ПОРАЖЕНИЕ"
    }
    private fun setTextRightAnswer() {
        findViewById<TextView>(R.id.correctWord).text = "Правильное слово - $answer"
    }
    private fun setEnabledButtons(isEnabled: Boolean = false) {
        buttonCheck.isEnabled = isEnabled
        findViewById<ImageButton>(R.id.buttonBack).isEnabled = isEnabled

        for (button in keyboardButton) {
            button.value.isEnabled = isEnabled
        }
    }
    fun showCustomToast(message: String) {
        val layout: FrameLayout = findViewById<FrameLayout>(R.id.toastFrameLayout)
        val textView: TextView = layout.findViewById(R.id.toastTextView)

        CustomToast.start(message, layout, textView, this)
    }

}