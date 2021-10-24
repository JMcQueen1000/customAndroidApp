package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.Card
import org.w3c.dom.Text

class DecklistAdaptor(private val data: List<DeckEntry>) : RecyclerView.Adapter<DecklistAdaptor.ViewHolder>()  {

    private var mListener: DeckListener? = null

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

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: DeckEntry) {
            deckName.text = item.deckName
            deckName.elevation = 20f

            v.setOnClickListener {
                mListener?.onClick(item)
            }

            v.setOnLongClickListener {
                mListener?.onLongClick(item)
                true
            }

            v.setOnTouchListener {  v, event ->
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

    interface DeckListener {
        fun onClick(deck: DeckEntry)
        fun onLongClick(deck: DeckEntry)
    }

    // Custom OnClickListener Setup
    fun setListener(listener : DeckListener) {
        mListener = listener
    }

}