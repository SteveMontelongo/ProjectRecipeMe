package com.recipeme.models


data class GroceryList (
    var name: String,
    val timeCreated: String,
    val items: List<Ingredient>
        )