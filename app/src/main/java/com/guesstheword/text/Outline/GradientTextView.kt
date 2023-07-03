package com.guesstheword.text.Outline

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class GradientTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    private val outlineColor = Color.BLACK
    private val outlineWidth = 8.0f

    private var gradientColors = intArrayOf(
        Color.parseColor("#8EFF00"),
        Color.parseColor("#D6FFA1")
    )

    fun setGradientColors(startColor: Int, endColor: Int) {
        gradientColors = intArrayOf(startColor, endColor)
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val x = 1f
        val y = height / 2.0f + textBounds.height() / 2.0f

        val outlinePaint = Paint(paint).apply {
            style = Paint.Style.STROKE
            strokeWidth = outlineWidth
            color = outlineColor
        }

        canvas.drawText(text, x, y, outlinePaint)

        val textShader = LinearGradient(
            x, y - textBounds.height(),
            x, y,
            gradientColors, null, Shader.TileMode.CLAMP
        )
        paint.shader = textShader

        canvas.drawText(text, x, y, paint)
    }


}
