package com.example.pokemontcgdeckbuilder

import io.pokemontcg.model.*

data class DeckCard(
    val card: Card,
    var quantity: Int,
    var position: Int
)