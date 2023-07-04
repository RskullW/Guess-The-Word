package com.guesstheword.stats

import com.guesstheword.database.DataBase

object GameSettings {
    var categoryName: String = ""
    var gameMode: String = ""
    var word: String = ""
    fun findWord() {
        var categories: MutableMap<String, Int> = mutableMapOf()
        var lengthWord: Int = 0

        if (categoryName == "all") {
            val randomCategory = DataBase.categoriesMap.entries.random()
            categories = randomCategory.value
        }

        else {
                categories = DataBase.categoriesMap[categoryName]
                    ?: DataBase.categoriesMap.entries.random().value
        }

        if (gameMode == "all") {
            val wordLengthOptions = listOf(5, 6, 7, 8)
            lengthWord = wordLengthOptions.random()
        }

        else {
            lengthWord = try {
                gameMode.toInt()
            } catch (exception: java.lang.Exception) {
                5
            }
        }

        val wordsWithMatchingLength = categories.filterValues { it == lengthWord }
        word = wordsWithMatchingLength.keys.random()
    }
}