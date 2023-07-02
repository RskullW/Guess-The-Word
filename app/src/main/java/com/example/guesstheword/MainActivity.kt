package com.example.guesstheword

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes.Margins
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

class MainActivity : AppCompatActivity() {
    private lateinit var buttonRandomWord: Button
    private var isButtonPressed: Boolean = false
    private val colorTextButtonPressed: Int = R.color.colorTextButtonPressed
    private val colorTextButtonNotPressed: Int = R.color.colorTextButtonNotPressed
    private var mapButtonMode: MutableMap<String, ButtonState> = mutableMapOf()
    private var mapButtonCategories: MutableMap<String, ButtonState> = mutableMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initializeButtonsMode()
        initializeButtonsCategories()
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
            }
        }
    }

    private fun initializeButtonsCategories() {

        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)

        var index: Int = 0
        var linearLayout: LinearLayout = createNewLinearLine(index)!!

        mapButtonCategories.put("all", ButtonState(button = findViewById<Button>(R.id.buttonRandomCategory1), _isPressed = false))


        for (i in 1..10) {

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

            button.text = "Temp№${index}"

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
            }
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
}