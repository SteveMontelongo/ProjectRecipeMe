package com.recipeme.models

data class Ingredient(
    var id: Int,
    var aisle: String,
    var amount: Double,
    var image: String,
    var name: String,
    var unit: String
)