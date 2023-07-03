package com.guesstheword.timer

import android.os.CountDownTimer

object CustomTimer {
    private var timer: CountDownTimer? = null
    private var currentTime: Long = 0
    private val maxTime: Long = 5999000

    fun start() {
        val initialTime = maxTime - currentTime

        timer = object : CountDownTimer(maxTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime = maxTime - millisUntilFinished
            }

            override fun onFinish() {
                currentTime = maxTime
            }
        }

        timer?.start()
    }

    fun stop() {
        timer?.cancel()
    }

    fun getTimerToString(): String {
        val minutes = currentTime / 60000
        val seconds = (currentTime % 60000) / 1000

        return String.format("%02d:%02d", minutes, seconds)
    }


}
