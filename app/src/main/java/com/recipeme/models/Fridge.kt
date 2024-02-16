package com.recipeme.models

data class Fridge(
    var name: String,
    var items: MutableList<Ingredient>
)