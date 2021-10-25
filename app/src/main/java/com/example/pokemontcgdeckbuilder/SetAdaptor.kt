package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Color
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.pokemontcg.model.CardSet

class SetAdaptor(private val data: List<CardSet>) : RecyclerView.Adapter<SetAdaptor.ViewHolder>()  {

    private var mListener: SetListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetAdaptor.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.setlist_layout, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SetAdaptor.ViewHolder,
                                  position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val setName: TextView = v.findViewById(R.id.setName)
        private val setLogo: ImageView = v.findViewById(R.id.setLogo)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: CardSet) {
            //bind item to view
            setName.text = item.name
            Picasso.get().load(item.logoUrl).into(setLogo)
            setLogo.elevation = 20f

            //set listener
            v.setOnClickListener {
                mListener?.onClick(item)
            }

            v.setOnLongClickListener {
                mListener?.onLongClick(item)
                true
            }
        }
    }

    interface SetListener {
        fun onClick(set: CardSet)
        fun onLongClick(set: CardSet)
    }

    // Custom OnClickListener Setup
    fun setListener(listener : SetListener) {
        mListener = listener
    }

}