package com.example.pokemontcgdeckbuilder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createDeckButton = findViewById<ImageView>(R.id.createDeckButton)
        val viewDeckButton = findViewById<ImageView>(R.id.viewDeckButton)
        val viewCardsButton = findViewById<ImageView>(R.id.viewCardsButton)

        createDeckButton.setOnClickListener {
            val intent = Intent(this, CreateDeck::class.java)
            startActivity(intent)
        }


        viewDeckButton.setOnClickListener {
            val intent = Intent(this, ViewDecks::class.java)
            startActivity(intent)
        }


        viewCardsButton.setOnClickListener {
            val intent = Intent(this, ViewCards::class.java)
            startActivity(intent)
        }

    }
}
