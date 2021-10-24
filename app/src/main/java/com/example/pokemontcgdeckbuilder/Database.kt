package com.example.pokemontcgdeckbuilder

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DeckEntry::class], version = 1)
@TypeConverters(DeckCardConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
}