package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.content.ClipData
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.Card

class CardDeckAdaptor(private val data: List<Card>) : RecyclerView.Adapter<CardDeckAdaptor.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDeckAdaptor.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.cardlist_layout, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CardDeckAdaptor.ViewHolder,
                                  position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val cardImage: ImageView = v.findViewById(R.id.cardImage)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: Card) {
            Picasso.get().load(item.imageUrl).into(cardImage)
            cardImage.setOnTouchListener { v, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val data = ClipData.newPlainText("inList", "true")
                        data.addItem(ClipData.newPlainText("card", item.id).getItemAt(0))
                        val shadowBuilder = View.DragShadowBuilder(v)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            v?.startDragAndDrop(data, shadowBuilder, v, 0)
                        } else {
                            v?.startDrag(data, shadowBuilder, v, 0)
                        }
                    }
                }

                v?.onTouchEvent(event) ?: true
            }
        }
    }

}