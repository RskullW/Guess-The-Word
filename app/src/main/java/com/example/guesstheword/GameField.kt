package com.example.guesstheword

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

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
    private var gameField: TableLayout

) {

    init {
        createGameField()
    }

    private fun createGameField() {
        for (row in 1..maxRows) {
            val rowLayout = getRowLayout(row)

            for (column in 1..maxColumns) {
                val cellLayout = createCellLayout()
                val textView = createTextView()

                cellLayout.addView(textView)
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
    private fun createCellLayout(): LinearLayout {
        val cellLayout = LinearLayout(context)
        val cellLayoutParams = TableRow.LayoutParams(
            0,
            TableRow.LayoutParams.MATCH_PARENT,
            1f
        )
        cellLayout.layoutParams = cellLayoutParams
        cellLayout.gravity = Gravity.CENTER
        cellLayout.setBackgroundResource(R.drawable.field_standart)
        return cellLayout
    }

    private fun createTextView(): TextView {
        val textView = TextView(context)
        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams = textLayoutParams
        textView.gravity = Gravity.CENTER
        textView.text = ""
        textView.setTextAppearance(context, R.style.TextField)
        return textView
    }


    fun setText(row: Int, column: Int, text: String) {
        val rowLayout = getRowLayout(row)
        val cellLayout = rowLayout.getChildAt(column - 1) as? LinearLayout
        val textView = cellLayout?.getChildAt(0) as? TextView
        textView?.text = text
    }

    fun getGameFieldLayout(): TableLayout {
        return gameField
    }
}