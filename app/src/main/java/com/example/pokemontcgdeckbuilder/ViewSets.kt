package com.example.pokemontcgdeckbuilder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.pokemontcg.Pokemon
import io.pokemontcg.model.CardSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class ViewSets : AppCompatActivity() {
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var setAdapter: SetAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_cards_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        getSets()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setQueryHint("Search Pokemon Cards")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(applicationContext, ViewSearchedCards::class.java)
                intent.putExtra("Query", query)
                startActivity(intent)
                return false
            }

        })

        return true
    }

    private fun showDetail(item: CardSet) {
        //setup bottom sheet
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.set_details, null)
        //set 'set' name and code
        view.findViewById<TextView>(R.id.nameOfSet).text = item.name
        view.findViewById<TextView>(R.id.codeOfSet).text = item.ptcgoCode
        //set 'set' details
        val totalCards = "Total Cards: " + item.totalCards.toString()
        view.findViewById<TextView>(R.id.totalCardsInSet).text = totalCards
        val series = "Series: " + item.series
        view.findViewById<TextView>(R.id.setSeries).text = series
        val releaseDate = "Date Released: " + item.releaseDate
        view.findViewById<TextView>(R.id.setReleaseDate).text = releaseDate
        Picasso.get().load(item.symbolUrl).into(view.findViewById<ImageView>(R.id.setSymbol))

        //bind view to sheet
        bottomSheet.setContentView(view)
        //show sheet
        bottomSheet.show()
    }

    private fun getSets()  {
        Toast.makeText(applicationContext, "Loading Sets", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            var sets = emptyList<CardSet>()
            try {
                val pokemon = Pokemon()
                sets = pokemon.set().all()
            } catch (e: Exception) {
                Log.i("API ERROR", "CARDS WOULD NOT LOAD")
                runOnUiThread {
                    Toast.makeText(applicationContext, "Loading Error: Could not connect to API", Toast.LENGTH_SHORT).show()
                }
            }
            runOnUiThread {
                assignSetRecyclerView(sets)
            }
        }
    }

    private fun assignSetRecyclerView(sets: List<CardSet>) {
        // Stuff that updates the UI

        val titleText = findViewById<TextView>(R.id.titleText)
        val text = "List of Released Sets"
        titleText.text = text

        //set up recycler view with adapter
        val setList = findViewById<RecyclerView>(R.id.setList)

        gridLayoutManager = GridLayoutManager(this, 2)
        setList.layoutManager = gridLayoutManager

        setAdapter = SetAdaptor(sets).also {
            it.setListener(object: SetAdaptor.SetListener {
                override fun onClick(set: CardSet) {
                    openSet(set)
                }

                override fun onLongClick(set: CardSet) {
                    showDetail(set)
                }
            })
        }
        setList.adapter = setAdapter
    }

    private fun openSet(set: CardSet) {
        val intent = Intent(this, ViewCards::class.java)
        intent.putExtra("Set Code", set.code)
        intent.putExtra("Set Name", set.name)
        startActivity(intent)
    }

}
