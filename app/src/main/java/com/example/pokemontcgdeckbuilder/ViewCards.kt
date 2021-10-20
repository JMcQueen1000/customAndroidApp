package com.example.pokemontcgdeckbuilder

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.pokemontcg.Pokemon
import io.pokemontcg.model.Card
import io.pokemontcg.model.CardSet
import java.util.concurrent.Executors


class ViewCards : AppCompatActivity() {
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var setAdapter: SetAdaptor
    private lateinit var cardAdapter: CardAdaptor
    //private val COUNTRY_NAME = "countryName"
    //private val COUNTRY_CODE = "countryCode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_cards_layout)

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            /**
             * Executes network task in background. You cannot
             * access view elements here.
             */

            val pokemon = Pokemon()

            //val cards = pokemon.card().all()
            //val card = pokemon.card().find("xy7-55")
            val sets = pokemon.set().all()

            // Get the list of card types i.e. Fire, Water, Electric, etc...
            //val typess = pokemon.type().all() // .observeAll() for RxJava2

            // Get a list of availble card super types i.e. Pokémon, Trainer, Energy
            //val superTypes = pokemon.superType().all() // .observeAll()

            // Get a list of card subtypes i.e. Basic, Stage 1, Item, Supporter, etc...
            //val subTypes = pokemon.subType().all() // .observeAll()

            //val cardss = pokemon.card()
            //    .where {
            //        //nationalPokedexNumber = 150
            //        //hp = 80.gt()
            //        // Alternatively "Water|Electric|Fire"
            //        //types = "Water" or "Electric" or "Fire"
            //        id = "xy7-54"
            //    }
            //    .all()

            runOnUiThread {
                // Stuff that updates the UI

                val titleText = findViewById<TextView>(R.id.titleText)
                val text = "List of Released Sets"
                titleText.text = text

                //set up recycler view with adapter
                val setList = findViewById<RecyclerView>(R.id.setList)

                gridLayoutManager = GridLayoutManager(this, 2)
                setList.layoutManager = gridLayoutManager

                setAdapter = SetAdaptor(sets) { showDetail(it) }
                setList.adapter = setAdapter
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
                loadSearchedCard(query)
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
        val series =  "Series: " + item.series
        view.findViewById<TextView>(R.id.setSeries).text = series
        val releaseDate = "Date Released: " + item.releaseDate
        view.findViewById<TextView>(R.id.setReleaseDate).text = releaseDate
        Picasso.get().load(item.symbolUrl).into(view.findViewById<ImageView>(R.id.setSymbol))

        //bind view to sheet
        bottomSheet.setContentView(view)
        //show sheet
        bottomSheet.show()
        
        loadCardSet(item.code, item.name)
    }

    private fun loadSearchedCard(searchText: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
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

                gridLayoutManager = GridLayoutManager(this, 2)
                setList.layoutManager = gridLayoutManager

                cardAdapter = CardAdaptor(cards) { showCardDetail(it) }
                setList.adapter = cardAdapter
            }
        }
    }

    private fun loadCardSet(code: String, name: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val pokemon = Pokemon()
            
            //search for all cards with the selected set name
            val cards = pokemon.card()
                .where { 
                    setCode = code
                    pageSize = 350
                }
                .all()

            val cardsSorted = cards.sortedWith(compareBy {it.number.toIntOrNull()})

            runOnUiThread {
                // Stuff that updates the UI

                val titleText = findViewById<TextView>(R.id.titleText)
                val text = "Cards from $name"
                titleText.text = text

                //set up recycler view with adapter
                val setList = findViewById<RecyclerView>(R.id.setList)

                gridLayoutManager = GridLayoutManager(this, 2)
                setList.layoutManager = gridLayoutManager

                cardAdapter = CardAdaptor(cardsSorted) { showCardDetail(it) }
                setList.adapter = cardAdapter
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
        val series =  "Number in Set: " + item.number
        view.findViewById<TextView>(R.id.numberOfCardInSet).text = series
        val releaseDate = "Rarity: " + item.rarity
        view.findViewById<TextView>(R.id.cardRarity).text = releaseDate
        Picasso.get().load(item.imageUrl).into(view.findViewById<ImageView>(R.id.cardSmallImage))

        //bind view to sheet
        bottomSheet.setContentView(view)
        //show sheet
        bottomSheet.show()
    }
}
