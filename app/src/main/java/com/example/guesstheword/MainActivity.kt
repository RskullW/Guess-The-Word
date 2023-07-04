package com.example.guesstheword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.guesstheword.button.state.ButtonState
import com.guesstheword.database.DataBase
import com.guesstheword.stats.GameSettings
import java.util.Locale.Category
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var buttonReady: Button
    private var mapButtonMode: MutableMap<String, ButtonState> = mutableMapOf()
    private var mapButtonCategories: MutableMap<String, ButtonState> = mutableMapOf()
    private val isSelectMode: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isSelectCategory: MutableLiveData<Boolean> = MutableLiveData(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        initializeButtonsMode()
        initializeButtonsCategories()
        initializeButtonReady()

    }

    private fun initializeButtonsMode() {
        val buttonIds = listOf(
            R.id.buttonRandomWord1,
            R.id.buttonRandomWord2,
            R.id.buttonRandomWord3,
            R.id.buttonRandomWord4,
            R.id.buttonRandomWord5
        )

        for (iteration in 0..4) {
            val buttonRandomWord = findViewById<Button>(buttonIds[iteration])
            val buttonState = ButtonState(button = buttonRandomWord, _isPressed = false)
            mapButtonMode.apply {
                put("buttonRandomWord${iteration+1}", buttonState)
            }
        }

        for (buttonState in mapButtonMode) {
            buttonState.value.button.setOnClickListener {
                var value: Boolean = buttonState.value.isPressed
                buttonState.value.isPressed = !value

                for (button in mapButtonMode) {
                    if (button == buttonState) continue
                    button.value.isPressed = false
                }

                isSelectMode.value = mapButtonMode.checkState()

            }
        }
    }
    private fun initializeButtonsCategories() {
        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)

        var index: Int = 0
        var linearLayout: LinearLayout = createNewLinearLine(index)!!

        mapButtonCategories.put("all", ButtonState(button = findViewById<Button>(R.id.buttonRandomCategory1), _isPressed = false))

        for (category in DataBase.categoriesMap) {
            val button = Button(this, null, 0, if (index%2 == 0) R.style.AppButtonStyleRight else R.style.AppButtonStyleLeft)

            val layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.button_width),
                resources.getDimensionPixelSize(R.dimen.button_height)
            )

            if (index % 2 == 1) {
                layoutParams.setMargins(0, 20.dpToPx(), 24.dpToPx(), 0)
            } else {
                layoutParams.setMargins(24.dpToPx(), 20.dpToPx(), 0, 0)
            }

            button.layoutParams = layoutParams

            button.text = category.key

            val buttonState = ButtonState(button = button, _isPressed = false)
            mapButtonCategories.apply {
                put(button.text as String, buttonState)
            }

            linearLayout.addView(mapButtonCategories[button.text]!!.button)

            var layout = createNewLinearLine(index)

            linearLayout = if (layout == null) {
                buttonContainer.addView(linearLayout)
                linearLayout
            } else layout

            index++
        }

        for (buttonState in mapButtonCategories) {
            buttonState.value.button.setOnClickListener {
                var value: Boolean = buttonState.value.isPressed
                buttonState.value.isPressed = !value

                for (button in mapButtonCategories) {
                    if (button == buttonState) continue
                    button.value.isPressed = false
                }

                isSelectCategory.value = mapButtonCategories.checkState()
            }
        }
    }
    private fun initializeButtonReady() {
        buttonReady = findViewById(R.id.buttonStartGame)

        buttonReady.setOnClickListener {
            for (buttonState in mapButtonMode) {
                if (buttonState.value.isPressed) {
                    GameSettings.gameMode = buttonState.value.button.contentDescription.toString()
                    break
                }
            }

            for (buttonState in mapButtonCategories) {
                if (buttonState.value.isPressed) {
                    GameSettings.categoryName = buttonState.value.button.text.toString()
                    break
                }
            }
            GameSettings.findWord()
            val intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        updateButtonVisibility()

        isSelectMode.observe(this, Observer { updateButtonVisibility() })
        isSelectCategory.observe(this, Observer { updateButtonVisibility() })
    }
    private fun updateButtonVisibility() {
        if (isSelectMode.value == true && isSelectCategory.value == true) {
            buttonReady.visibility = if (buttonReady.visibility != View.VISIBLE) {
                val anim = AnimationUtils.loadAnimation(this, R.anim.button_appear)
                buttonReady.startAnimation(anim)
                View.VISIBLE
            } else buttonReady.visibility

        } else {
            buttonReady.visibility = if (buttonReady.visibility != View.GONE) {

                val anim = AnimationUtils.loadAnimation(this, R.anim.button_disappears)
                buttonReady.startAnimation(anim)
                View.GONE
            } else buttonReady.visibility
        }
    }
    private fun createNewLinearLine(index: Int):LinearLayout? {
        if (index % 2 == 0) {
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.gravity = Gravity.CENTER_HORIZONTAL

            return linearLayout
        }

        return null
    }
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
    private fun Map<String, ButtonState>.checkState(): Boolean {
        for (value in values) {
            if (value.isPressed) {
                return true
            }
        }

        return false
    }
}