package com.example.pokemontcgdeckbuilder

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.pokemontcg.Pokemon
import io.pokemontcg.model.Card
import java.util.concurrent.Executors

class CreateDeck : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var cardAdaptor: CardDeckAdaptor
    private lateinit var deckAdaptor: DeckAdaptor
    private lateinit var currentCards: List<Card>
    private lateinit var deckCards: MutableList<DeckCard>
    private lateinit var loadedDeck: DeckEntry
    private var load: Boolean = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_deck_layout)


        //check intent data to see if a deck should be loaded
        val deckID = intent.getIntExtra("id", 0)
        if (deckID != 0) {
            loadDeckFromDatabase(deckID)
        }

        setUpDeckRecyclerView(load)
        setUpCardRecyclerView()

        val saveButton = findViewById<Button>(R.id.saveDeckButton)
        saveButton.setOnClickListener {
            if (findViewById<EditText>(R.id.editDeckText).text.toString() != "") {
                val intent = Intent(this, MainActivity::class.java)

                saveToDatabase()

                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Please enter a deck name to save", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpCardRecyclerView() {
        currentCards = emptyList()

        //set up cardList recycler view
        val cardList = findViewById<RecyclerView>(R.id.cardRecyclerView)
        cardList.elevation = 20f

        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cardList.layoutManager = linearLayoutManager

        cardAdaptor = CardDeckAdaptor(currentCards)
        cardList.adapter = cardAdaptor

        cardList.setOnDragListener {v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (event.clipDescription.label.toString() == "inDeck") {
                        // As an example of what your application might do,
                        // applies a new background to the View to indicate that it can accept
                        // data.
                        (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners_enable,theme)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        // returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners_hover,theme)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners_enable,theme)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    // Gets the item containing the dragged data
                    val item: ClipData.Item = event.clipData.getItemAt(1)

                    // Gets the text data from the item.
                    val dragData = item.text

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is $dragData", Toast.LENGTH_SHORT).show()

                    // Turns off any color tints
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners,theme)

                    // Invalidates the view to force a redraw
                    v.invalidate()

                    removeCardFromDeck(dragData.toString())

                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    //returns background color to normal
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners,theme)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                else -> true
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpDeckRecyclerView(load: Boolean) {
        //load card if load is initiated, else start with empty deck
        if (load) {
            deckCards = loadedDeck.deck as MutableList<DeckCard>
            //set deck name
            findViewById<EditText>(R.id.editDeckText).setText(loadedDeck.deckName)
        }
        else {
            deckCards = mutableListOf()
        }

        //set up deckList recycler view
        val deckList = findViewById<RecyclerView>(R.id.deckRecyclerView)
        deckList.elevation = 20f

        gridLayoutManager = GridLayoutManager(this, 5)
        deckList.layoutManager = gridLayoutManager

        currentCards = emptyList()
        deckAdaptor = DeckAdaptor(deckCards) {showCardDetail(it)}
        deckList.adapter = deckAdaptor

        deckList.setOnDragListener {v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (event.clipDescription.label.toString() == "inList") {
                        // As an example of what your application might do,
                        // applies a new background to the View to indicate that it can accept
                        // data.
                        (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners_enable,theme)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        // returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies new background to the View. Return true; the return value is ignored.
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners_hover,theme)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners_enable,theme)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    // Gets the item containing the dragged data
                    val item: ClipData.Item = event.clipData.getItemAt(1)

                    // Gets the text data from the item.
                    val dragData = item.text

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is $dragData", Toast.LENGTH_SHORT).show()

                    // Turns off any color tints
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners,theme)

                    // Invalidates the view to force a redraw
                    v.invalidate()

                    addCardToDeck(dragData.toString())

                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    //returns background color to normal
                    (v as? RecyclerView)?.background = resources.getDrawable(R.drawable.background_corners,theme)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                else -> true
            }

        }
    }

    private fun loadDeckFromDatabase(id: Int) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()

            val deckDao = db.deckDao()

            loadedDeck = deckDao.findByID(id)

            load = true
            setUpDeckRecyclerView(load)
        }
    }

    private fun saveToDatabase() {
        //DATABASE STUFF
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()

            val deckDao = db.deckDao()

            val deckName = findViewById<EditText>(R.id.editDeckText)
            if (load) {
                deckDao.updateDeckName(loadedDeck.uid, deckName.text.toString())
                deckDao.updateDeckCards(loadedDeck.uid, deckCards)
                runOnUiThread {
                    Toast.makeText(this, "Deck Saved", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                if (deckCards.isNotEmpty()) {
                    val newDeck = DeckEntry(0, deckName.text.toString(), deckCards)
                    deckDao.insertAll(newDeck)
                    runOnUiThread {
                        Toast.makeText(this, "Deck Saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showCardDetail(item: DeckCard) {
        //setup bottom sheet
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.card_details, null)
        //set card name and code
        view.findViewById<TextView>(R.id.nameOfCard).text = item.card.name
        //set other card details
        val totalCards = "Set: " + item.card.set
        view.findViewById<TextView>(R.id.setCardIsFrom).text = totalCards
        val series =  "Number in Set: " + item.card.number
        view.findViewById<TextView>(R.id.numberOfCardInSet).text = series
        val releaseDate = "Rarity: " + item.card.rarity
        view.findViewById<TextView>(R.id.cardRarity).text = releaseDate
        Picasso.get().load(item.card.imageUrlHiRes).into(view.findViewById<ImageView>(R.id.cardSmallImage))

        //bind view to sheet
        bottomSheet.setContentView(view)
        //show sheet
        bottomSheet.show()
    }

    private fun removeCardFromDeck(item: String) {
        var removeCard = false

        val cardToRemove = deckCards.find {
            it.card.id == item
        }

        if (cardToRemove != null) {
            //decrease quantity of current card multiple in deck
            deckCards.filter {
                if (it.card.id == cardToRemove.card.id) {
                    it.quantity--
                    if (it.quantity <= 0) {
                        removeCard = true;
                    }
                }
                 true
            }

            if (removeCard) {
                //remove card from deck
                deckCards.remove(cardToRemove)
                deckCards.filter {
                    if (it.position > cardToRemove.position) {
                        it.position--
                    }
                    true
                }
                deckAdaptor.notifyItemRemoved(cardToRemove.position)
            }
            else {
                deckAdaptor.notifyItemChanged(cardToRemove.position, null)
            }
        }
    }

    private fun addCardToDeck(item: String) {
        val newCard = currentCards.find {
            it.id == item
        }

        if (newCard != null) {
            //check if same pokemon name is added more than 4 times
            var nameCount = 0
            deckCards.filter {
                if (it.card.name == newCard.name) {
                    nameCount = nameCount + it.quantity
                }
                true
            }

            var copyCard = deckCards.find {
                it.card.id == newCard.id
            }
            if (copyCard != null) {
                deckCards.filter {
                    if (nameCount < 4) {
                        if (it.card.id == copyCard.card.id) {
                            if (it.quantity < 4) {
                                it.quantity = it.quantity + 1
                                deckAdaptor.notifyItemChanged(copyCard.position, null)
                            }
                            else {
                                Toast.makeText(this, "Cannot add more than 4 cards with the same name", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    true
                }
            }
            else {
                if (nameCount < 4) {
                    val brandNewCard = DeckCard(newCard, 1, deckCards.size)
                    deckCards.add(brandNewCard)
                    deckAdaptor.notifyItemChanged(deckAdaptor.itemCount)
                }
                else {
                    Toast.makeText(this, "Cannot add more than 4 cards with the same name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadSearchedCard(searchText: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            val pokemon = Pokemon()

            //search for all cards with the selected set name
            currentCards = pokemon.card()
                .where {
                    name = searchText
                    pageSize = 10000
                }
                .all()

            runOnUiThread {
                // Stuff that updates the UI

                //set up recycler view with adapter
                val cardList = findViewById<RecyclerView>(R.id.cardRecyclerView)

                linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                cardList.layoutManager = linearLayoutManager

                cardAdaptor = CardDeckAdaptor(currentCards)
                cardList.adapter = cardAdaptor
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
}
