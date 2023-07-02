package com.example.guesstheword

import android.widget.Button

class ButtonState(private var _isPressed: Boolean, val button: Button) {

    var isPressed: Boolean
        get() = _isPressed
        set(value) {
            _isPressed = value
            updateButtonColor()
        }

    private fun updateButtonColor() {
        if (_isPressed) {
            button.isActivated = true
            button.setTextColor(button.context.getColor(R.color.colorTextButtonPressed))
        } else {
            button.isActivated = false
            button.setTextColor(button.context.getColor(R.color.colorTextButtonNotPressed))
        }
    }
}
