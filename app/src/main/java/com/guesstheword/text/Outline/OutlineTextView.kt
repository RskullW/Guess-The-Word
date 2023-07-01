package com.guesstheword.text.Outline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class OutlineTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    private val outlineColor = Color.BLACK
    private val outlineWidth = 10.0f

    override fun onDraw(canvas: Canvas) {
        val textColor = currentTextColor

        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = outlineWidth
        paint.color = outlineColor
        paint.textSize = textSize
        paint.typeface = typeface
        paint.textAlign = Paint.Align.CENTER

        val text = text.toString()

        val x = width / 2.0f
        val y = (height / 2.0f) - ((paint.descent() + paint.ascent()) / 2.0f)

        canvas.drawText(text, x, y, paint)

        paint.style = Paint.Style.FILL
        paint.color = textColor
        canvas.drawText(text, x, y, paint)
    }
}
