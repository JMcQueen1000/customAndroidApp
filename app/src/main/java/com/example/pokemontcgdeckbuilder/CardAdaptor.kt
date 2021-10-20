package com.example.pokemontcgdeckbuilder

import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.view.ContentInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.Card
import io.pokemontcg.model.CardSet

class CardAdaptor(private val data: List<Card>,
                 private val listener: (Card) -> Unit) : RecyclerView.Adapter<CardAdaptor.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdaptor.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.cardlist_layout, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CardAdaptor.ViewHolder,
                                  position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val cardImage: ImageView = v.findViewById(R.id.cardImage)

        fun bind(item: Card) {
            Picasso.get().load(item.imageUrl).into(cardImage);

            v.setOnClickListener { listener(item) }
        }
    }

}