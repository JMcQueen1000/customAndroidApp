package com.example.pokemontcgdeckbuilder


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.Card

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
            cardImage.elevation = 20f

            v.setOnClickListener { listener(item) }
        }
    }

}