package com.example.pokemontcgdeckbuilder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeckEntry(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "deck_name") val deckName: String?,
    @ColumnInfo(name = "deck") val deck: List<DeckCard>?
)