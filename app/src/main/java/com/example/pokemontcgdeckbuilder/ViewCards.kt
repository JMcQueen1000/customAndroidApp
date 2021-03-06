package com.example.pokemontcgdeckbuilder

import android.content.Intent
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


class ViewCards : AppCompatActivity() {
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var cardAdapter: CardAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_cards_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val setName = intent.getStringExtra("Set Name")
        val setCode = intent.getStringExtra("Set Code")

        if (setName != null) {
            if (setCode != null) {
                loadCardSet(setCode, setName)
            }
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
                val intent = Intent(applicationContext, ViewSearchedCards::class.java)
                intent.putExtra("Query", query)
                startActivity(intent)
                return false
            }
        })
        return true
    }

    private fun loadCardSet(code: String, name: String) {
        Toast.makeText(applicationContext, "Loading Cards", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pokemon = Pokemon()

                //search for all cards with the selected set name
                val cards = pokemon.card()
                    .where {
                        setCode = code
                        pageSize = 350
                    }
                    .all()

                val cardsSorted = cards.sortedWith(compareBy { it.number.toIntOrNull() })

                runOnUiThread {
                    // Stuff that updates the UI

                    val titleText = findViewById<TextView>(R.id.titleText)
                    val text = "Cards from $name"
                    titleText.text = text

                    //set up recycler view with adapter
                    val setList = findViewById<RecyclerView>(R.id.setList)

                    gridLayoutManager = GridLayoutManager(applicationContext, 2)
                    setList.layoutManager = gridLayoutManager

                    cardAdapter = CardAdaptor(cardsSorted) { showCardDetail(it) }
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
