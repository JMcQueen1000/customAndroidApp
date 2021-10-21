package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
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


        createDeckButton.setOnTouchListener {  v, event ->
            when(event?.action){
                MotionEvent.ACTION_DOWN -> {
                    (v as? ImageView)?.setColorFilter(Color.BLACK,android.graphics.PorterDuff.Mode.OVERLAY)
                }
                MotionEvent.ACTION_UP -> {
                    (v as? ImageView)?.clearColorFilter()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        viewDeckButton.setOnTouchListener {  v, event ->
            when(event?.action){
                MotionEvent.ACTION_DOWN -> {
                    (v as? ImageView)?.setColorFilter(Color.BLACK,android.graphics.PorterDuff.Mode.OVERLAY)
                }
                MotionEvent.ACTION_UP -> {
                    (v as? ImageView)?.clearColorFilter()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        viewCardsButton.setOnTouchListener {  v, event ->
            when(event?.action){
                MotionEvent.ACTION_DOWN -> {
                    (v as? ImageView)?.setColorFilter(Color.BLACK,android.graphics.PorterDuff.Mode.OVERLAY)
                }
                MotionEvent.ACTION_UP -> {
                    (v as? ImageView)?.clearColorFilter()
                }
            }

            v?.onTouchEvent(event) ?: true
        }


    }
}
