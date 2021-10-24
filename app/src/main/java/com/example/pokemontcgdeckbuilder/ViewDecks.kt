package com.example.pokemontcgdeckbuilder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import io.pokemontcg.model.Card
import java.util.concurrent.Executors

class ViewDecks : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var decklistAdaptor: DecklistAdaptor
    private lateinit var decks: List<DeckEntry>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_decks_layout)

        //set decks from database
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()

            val deckDao = db.deckDao()
            decks = deckDao.getAll()

            runOnUiThread {
                //set up deckList recycler view
                val deckList = findViewById<RecyclerView>(R.id.viewDeckRecyclerView)
                deckList.elevation = 20f

                linearLayoutManager = LinearLayoutManager(this)
                deckList.layoutManager = linearLayoutManager

                decklistAdaptor = DecklistAdaptor(decks) {editDeck(it)}
                deckList.adapter = decklistAdaptor
            }
        }
    }

    private fun editDeck(it: DeckEntry) {
        val intent = Intent(this, CreateDeck::class.java)
        intent.putExtra("id", it.uid)
        startActivity(intent)
    }
}
