package com.example.pokemontcgdeckbuilder

import androidx.room.*

@Dao
interface DeckDao {
    @Query("SELECT * FROM DeckEntry")
    fun getAll(): MutableList<DeckEntry>

    @Query("SELECT * FROM DeckEntry WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): MutableList<DeckEntry>

    @Query("SELECT * FROM DeckEntry WHERE uid LIKE :first LIMIT 1")
    fun findByID(first: Int): DeckEntry

    @Insert
    fun insertAll(vararg deck: DeckEntry)

    @Query("UPDATE DeckEntry SET deck = :editedDeck WHERE uid = :id")
    fun updateDeckCards(id: Int, editedDeck: List<DeckCard>)

    @Query("UPDATE DeckEntry SET deck_name = :deckName WHERE uid = :id")
    fun updateDeckName(id: Int, deckName: String)

    @Delete
    fun delete(deck: DeckEntry)
}