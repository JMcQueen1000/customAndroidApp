package com.example.pokemontcgdeckbuilder

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.Card
import org.w3c.dom.Text

class DecklistAdaptor(private val data: List<DeckEntry>,
                  private val listener: (DeckEntry) -> Unit) : RecyclerView.Adapter<DecklistAdaptor.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecklistAdaptor.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.individual_deck_list, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: DecklistAdaptor.ViewHolder,
                                  position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val deckName: TextView = v.findViewById(R.id.deckName)

        fun bind(item: DeckEntry) {
            deckName.text = item.deckName
            deckName.elevation = 20f

            v.setOnClickListener { listener(item) }
        }
    }

}