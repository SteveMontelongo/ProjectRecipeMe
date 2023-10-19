package com.recipeme.models


data class GroceryList (
    val name: String,
    val timeCreated: String,
    val items: List<Ingredient>
        )