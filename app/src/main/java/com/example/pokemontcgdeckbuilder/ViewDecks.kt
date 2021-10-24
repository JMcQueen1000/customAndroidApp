package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.w3c.dom.Text
import java.lang.Thread.sleep
import java.util.concurrent.Executors

class ViewDecks : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var decklistAdaptor: DecklistAdaptor
    private lateinit var decks: MutableList<DeckEntry>
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

                decklistAdaptor = DecklistAdaptor(decks).also {
                    it.setListener(object: DecklistAdaptor.DeckListener {
                        override fun onClick(deck: DeckEntry) {
                            editDeck(deck)
                        }

                        override fun onLongClick(deck: DeckEntry) {
                            deleteDeckPopup(deck)
                        }
                    })
                }
                deckList.adapter = decklistAdaptor
            }
        }
    }

    private fun editDeck(deck: DeckEntry) {
        val intent = Intent(this, CreateDeck::class.java)
        intent.putExtra("id", deck.uid)
        startActivity(intent)
    }

    private fun deleteDeckPopup(deck: DeckEntry) {
        //setup bottom sheet
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.delete_deck_dialog, null)

        //set buttons with listeners
        val yesButton = view.findViewById<Button>(R.id.yesButton)
        yesButton.setOnClickListener {
            deleteDeckFromDatabase(deck)

            //update decklistAdaptor
            decks.remove(deck)
            decklistAdaptor.notifyItemRangeRemoved(0, decks.size+1)

            bottomSheet.dismiss()
            Toast.makeText(this, "Deck successfully deleted", Toast.LENGTH_SHORT).show()
        }

        val noButton = view.findViewById<Button>(R.id.noButton)
        noButton.setOnClickListener {
            bottomSheet.dismiss()
        }

        //bind view to sheet
        bottomSheet.setContentView(view)
        //show sheet
        bottomSheet.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteDeckFromDatabase(deck: DeckEntry) {
        //delete deck from database
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()

            val deckDao = db.deckDao()
            deckDao.delete(deck)
        }
    }
}
