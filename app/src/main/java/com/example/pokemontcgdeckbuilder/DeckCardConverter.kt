package com.example.pokemontcgdeckbuilder

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class DeckCardConverter {
    @TypeConverter
    fun stringToDeckCard(data: String?): List<DeckCard>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<DeckCard>?>() {}.type
        return Gson().fromJson<List<DeckCard>>(data, listType)
    }

    @TypeConverter
    fun deckCardToString(someObjects: List<DeckCard?>?): String? {
        return Gson().toJson(someObjects)
    }
}