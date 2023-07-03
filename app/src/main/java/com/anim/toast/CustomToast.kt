package com.anim.toast

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import com.example.guesstheword.R

object CustomToast {
    private var millis: Int = 1000
    private var isToastVisible = false

    fun setDuration(timeInMillis: Int) {
        millis = timeInMillis
    }

    fun start(message: String, frameLayout: FrameLayout,  textView: TextView,context: Context) {
        textView.text = message

        if (!isToastVisible) {
            frameLayout.visibility = if (frameLayout.visibility != View.VISIBLE) {
                val anim = AnimationUtils.loadAnimation(context, R.anim.toast_appear)
                isToastVisible = true
                frameLayout.startAnimation(anim)
                View.VISIBLE
            } else frameLayout.visibility

            Handler().postDelayed({
                val anim = AnimationUtils.loadAnimation(context, R.anim.toast_disappears)
                frameLayout.startAnimation(anim)
                frameLayout.visibility = View.GONE
                isToastVisible = false
            }, millis.toLong() * 4)
        }
    }
}