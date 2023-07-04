package com.example.guesstheword

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat

class GameField(
    private val maxColumns: Int,
    private val maxRows: Int = 5,
    private val firstRow: TableRow,
    private val secondRow: TableRow,
    private val thirdRow: TableRow,
    private val fourthRow: TableRow,
    private val fifthRow: TableRow,
    private val sixthRow: TableRow,
    private val context: Context,
    private var gameField: TableLayout,
    private var isFinish: Boolean,
) {
    private var numberLine: UShort = 1U
    var nowRow: Int = 1
        private set
    var nowColumn: Int = 1
        private set
    init {
        createGameField()
    }

    private fun createGameField() {
        for (row in 1..maxRows) {
            val rowLayout = getRowLayout(row)

            for (column in 1..maxColumns) {
                val cellLayout = createCellLayout()
                val editText = createEditText(row, column)

                if (!isFinish) {
                    cellLayout.addView(editText)
                } else {
                    val emptyView = View(context)
                    val layoutParams = LinearLayout.LayoutParams(
                        0,
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    cellLayout.addView(emptyView, layoutParams)
                }
                rowLayout.addView(cellLayout)
            }
        }
    }

    private fun getRowLayout(row: Int): TableRow {
        return when (row) {
            1 -> firstRow
            2 -> secondRow
            3 -> thirdRow
            4 -> fourthRow
            5 -> fifthRow
            6 -> sixthRow
            else -> throw IllegalArgumentException("Invalid row number: $row")
        }
    }
    fun setVisibleField(startRow: Int, endRow: Int, isVisible: Boolean) {
        for (row in startRow..endRow) {
            val rowLayout = getRowLayout(row)

            for (column in 1..maxColumns) {
                val rowLayout = getRowLayout(row)
                val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout

                cellLayout?.visibility = View.INVISIBLE
            }
        }
    }
    private fun createCellLayout(): LinearLayout {
        val cellLayout = LinearLayout(context)
        val cellLayoutParams = TableRow.LayoutParams(
            if (!isFinish) 0 else TableRow.LayoutParams.WRAP_CONTENT,
            if (!isFinish) TableRow.LayoutParams.MATCH_PARENT else TableRow.LayoutParams.WRAP_CONTENT,
            if (!isFinish) 1f else 1f,
        )
        cellLayout.layoutParams = cellLayoutParams
        cellLayout.gravity = Gravity.CENTER
        cellLayout.setBackgroundResource(R.drawable.field_standart)
        return cellLayout
    }
    private fun createEditText(row: Int, column: Int): EditText {
        val editText = EditText(context)
        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editText.layoutParams = textLayoutParams
        editText.gravity = Gravity.CENTER
        editText.inputType = InputType.TYPE_NULL
        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
        editText.maxLines = 1
        if (!isFinish) editText.setTextAppearance(context, R.style.EditTextField)
        editText.isCursorVisible = !isFinish
        editText.contentDescription = "$row$column"
        editText.setTextColor(Color.BLACK)

        if (row != numberLine.toInt() || isFinish) {
            editText.isEnabled = false
        }

        if (!isFinish) {
            editText.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    nowRow = editText.contentDescription.toString().toInt() / 10
                    nowColumn = editText.contentDescription.toString().toInt() % 10
                }
                false
            }
        }

        return editText
    }
    private fun disableAllLine() {
        for (row in 1..maxRows) {
            if (row == nowRow) {
                continue
            }

            for (column in 1..maxColumns) {
                val rowLayout = getRowLayout(row)
                val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout
                val editText = cellLayout?.getChildAt(0) as? EditText

                editText?.isEnabled = false
            }
        }
    }
    fun setStyleField(row: Int, column: Int, drawable: Drawable) {
        val rowLayout = getRowLayout(row)
        val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout

        cellLayout?.background = drawable
    }
    fun setStyleField(row: Int, column: Int, fieldState: FieldState) {
        val rowLayout = getRowLayout(row)
        val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout

        val currentBackground = cellLayout?.background

        val newBackground = when (fieldState) {
            FieldState.CORRECT -> ContextCompat.getDrawable(context, R.drawable.field_correct)
            FieldState.INCORRECT -> ContextCompat.getDrawable(context, R.drawable.field_incorrect)
            FieldState.POSSIBLY -> ContextCompat.getDrawable(context, R.drawable.field_possibly)
            else -> ContextCompat.getDrawable(context, R.drawable.field_standart)
        }

        if (currentBackground != null && newBackground != null) {
            val duration = 500 // Transition duration in milliseconds

            // Create a TransitionDrawable with the current and new backgrounds
            val transitionDrawable = TransitionDrawable(arrayOf(currentBackground, newBackground))

            // Set the transition duration for the TransitionDrawable
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(duration)

            // Set the TransitionDrawable as the background for the cell layout
            cellLayout?.background = transitionDrawable

            // Start the transition animation
            transitionDrawable.startTransition(duration)

            // Apply fade-in effect on the new background
            val alphaAnimation = AlphaAnimation(0f, 1f)
            alphaAnimation.duration = duration.toLong()
            cellLayout?.startAnimation(alphaAnimation)
        } else {
            // Set the new background directly if there is no current background
            cellLayout?.setBackgroundResource(
                when (fieldState) {
                    FieldState.CORRECT -> R.drawable.field_correct
                    FieldState.INCORRECT -> R.drawable.field_incorrect
                    FieldState.POSSIBLY -> R.drawable.field_possibly
                    else -> R.drawable.field_standart
                }
            )
        }
    }
    fun setNumberLine(row: Int, correctSymbols: MutableMap<Int, Char>) {
        nowRow = row
        val rowLayout = getRowLayout(nowRow)

        for (column in 1..  maxColumns) {

            val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout
            val editText = cellLayout?.getChildAt(0) as? EditText
            editText?.isEnabled = true

            if (correctSymbols.containsKey(column)) {
                editText?.setHint(correctSymbols[column].toString())
            }

            if (column == 1) {
                editText?.requestFocus()
            }
        }

        nowColumn = 1
        disableAllLine()

    }
    fun setText(row: Int, column: Int, text: String) {
        val rowLayout = getRowLayout(row)
        val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout
        val editText = cellLayout?.getChildAt(0) as? EditText
        editText?.setText(text)
        editText?.clearFocus()
    }
    fun setText(row: Int, column: Int, text: String, nextColumn: Int) {
        val rowLayout = getRowLayout(row)

        val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout
        val cellLayoutNext = rowLayout.getChildAt(nextColumn - 1) as? LinearLayout

        val editText = cellLayout?.getChildAt(0) as? EditText
        val editTextNext = cellLayoutNext?.getChildAt(0) as? EditText

        editText?.setText(text)

        nowColumn = nextColumn

        editText?.clearFocus()
        editTextNext?.requestFocus()
    }
    fun getWord(row: Int): String {
        var answerUser: String = ""

        val rowLayout = getRowLayout(row)
        for (column in 1..maxColumns) {
            val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout
            val editText = cellLayout?.getChildAt(0) as? EditText

            answerUser += editText?.text
        }

        return answerUser
    }
    fun getBackgroundStyle(row: Int, column: Int): Drawable? {
        val rowLayout = getRowLayout(row)
        val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout

        return cellLayout?.background
    }
}