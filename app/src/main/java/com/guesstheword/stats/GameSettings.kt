package com.guesstheword.stats

import android.content.Context
import android.util.Log
import com.guesstheword.database.DataBase

object GameSettings {
    var categoryName: String = ""
    var gameMode: String = ""
    var word: String = ""

    var nowCategory: String = ""
    var victoryRounds: Int = 0
    var defeatRounds: Int = 0
    var categoriesPlayed: MutableMap<String, UInt> = mutableMapOf()

    private val PREF_NAME = "game_stats"
    private val KEY_VICTORY_ROUNDS = "victory_rounds"
    private val KEY_DEFEAT_ROUNDS = "defeat_rounds"
    private val KEY_CATEGORIES_PLAYED = "categories_played"

    fun findWord() {
        var categories: MutableMap<String, Int>
        var lengthWord: Int = 0

        if (categoryName == "all") {
            val randomCategory = DataBase.categoriesMap.entries.random()
            categories = randomCategory.value
            nowCategory = randomCategory.key
        }

        else {
            nowCategory = categoryName
                categories = if(DataBase.categoriesMap[categoryName] != null) DataBase.categoriesMap[categoryName]!! else
                {
                    val randomCategory = DataBase.categoriesMap.entries.random()
                    nowCategory = randomCategory.key
                    randomCategory.value
                }
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

    fun saveStats(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_VICTORY_ROUNDS, victoryRounds)
        editor.putInt(KEY_DEFEAT_ROUNDS, defeatRounds)
        editor.putStringSet(KEY_CATEGORIES_PLAYED, categoriesPlayed.map { "${it.key}:${it.value}" }.toSet())
        editor.apply()

    }

    fun resetStats(context: Context) {
        victoryRounds = 0
        defeatRounds = 0
        categoriesPlayed.clear()
        saveStats(context)
    }

    fun loadStats(context:Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        victoryRounds = sharedPreferences.getInt(KEY_VICTORY_ROUNDS, 0)
        defeatRounds = sharedPreferences.getInt(KEY_DEFEAT_ROUNDS, 0)


        val categoriesPlayedSet = sharedPreferences.getStringSet(KEY_CATEGORIES_PLAYED, emptySet())
        categoriesPlayed = categoriesPlayedSet?.associate {
            val (category, count) = it.split(":")
            category to count.toUInt()
        }?.toMutableMap() ?: mutableMapOf()

    }
}