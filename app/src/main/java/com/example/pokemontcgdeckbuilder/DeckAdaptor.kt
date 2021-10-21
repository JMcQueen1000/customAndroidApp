package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.content.ClipData
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.Card
import io.pokemontcg.model.CardSet

class DeckAdaptor(private val data: List<DeckCard>,
                  private val listener: (DeckCard) -> Unit) : RecyclerView.Adapter<DeckAdaptor.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckAdaptor.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.deck_row_details, parent, false) as View

        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: DeckAdaptor.ViewHolder,
                                  position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val cardImage: ImageView = v.findViewById(R.id.deckCardImage)
        private val cardQuantity: TextView = v.findViewById(R.id.cardQuantityInDeck)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: DeckCard) {
            cardQuantity.text = item.quantity.toString()
            Picasso.get().load(item.card.imageUrl).into(cardImage)
            v.setOnLongClickListener { v ->
                val data = ClipData.newPlainText("inDeck", "true")
                data.addItem(ClipData.newPlainText("card", item.card.id).getItemAt(0))
                val shadowBuilder = View.DragShadowBuilder(v)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    v?.startDragAndDrop(data, shadowBuilder, v, 0)
                }
                else {
                    v?.startDrag(data, shadowBuilder, v, 0)

                }
                true
            }

            v.setOnClickListener { listener(item) }
        }
    }

}