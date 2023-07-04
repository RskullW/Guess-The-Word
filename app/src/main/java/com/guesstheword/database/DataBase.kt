package com.guesstheword.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object DataBase {
    val categoriesMap: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    fun fetchCategories(callback: DataCallback) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Categories")

        collectionRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val category = document.id
                val categoryData = document.data

                val fieldsMap: MutableMap<String, Int> = mutableMapOf()

                if (categoryData != null) {
                    for ((field, value) in categoryData) {
                        if (field != "documentId") {
                            val word = field.toString()
                            val wordLength = value.toString().toInt()
                            fieldsMap[word] = wordLength
                        }
                    }
                }

                categoriesMap[category] = fieldsMap
            }

            callback.onDataLoaded()
        }.addOnFailureListener { exception ->
            Log.d("Error", "Ошибка при получении данных из Firestore: ${exception.message}")
            callback.onDataLoaded()

        }
    }
}