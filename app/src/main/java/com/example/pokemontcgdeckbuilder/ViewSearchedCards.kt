package com.example.pokemontcgdeckbuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import io.pokemontcg.model.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class ViewSearchedCards : AppCompatActivity() {
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var cardAdapter: CardAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_cards_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val query = intent.getStringExtra("Query")

        if (query != null) {
            loadSearchedCard(query)
        }
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
                loadSearchedCard(query)
                return false
            }
        })
        return true
    }

    private fun loadSearchedCard(searchText: String) {
        Toast.makeText(applicationContext, "Searching Cards", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pokemon = Pokemon()

                //search for all cards with the selected set name
                val cards = pokemon.card()
                    .where {
                        name = searchText
                        pageSize = 10000
                    }
                    .all()

                runOnUiThread {
                    // Stuff that updates the UI

                    val titleText = findViewById<TextView>(R.id.titleText)
                    val text = "Cards with '$searchText' in name"
                    titleText.text = text

                    //set up recycler view with adapter
                    val setList = findViewById<RecyclerView>(R.id.setList)

                    gridLayoutManager = GridLayoutManager(applicationContext, 2)
                    setList.layoutManager = gridLayoutManager

                    cardAdapter = CardAdaptor(cards) { showCardDetail(it) }
                    setList.adapter = cardAdapter
                }
            }
            catch (e : Exception) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Loading Error: Could not connect to API", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCardDetail(item: Card) {
        //setup bottom sheet
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.card_details, null)
        //set card name and code
        view.findViewById<TextView>(R.id.nameOfCard).text = item.name
        //set other card details
        val totalCards = "Set: " + item.set
        view.findViewById<TextView>(R.id.setCardIsFrom).text = totalCards
        val series = "Number in Set: " + item.number
        view.findViewById<TextView>(R.id.numberOfCardInSet).text = series
        val releaseDate = "Rarity: " + item.rarity
        view.findViewById<TextView>(R.id.cardRarity).text = releaseDate
        Picasso.get().load(item.imageUrlHiRes)
            .into(view.findViewById<ImageView>(R.id.cardSmallImage))

        //bind view to sheet
        bottomSheet.setContentView(view)
        //show sheet
        bottomSheet.show()
    }
}
