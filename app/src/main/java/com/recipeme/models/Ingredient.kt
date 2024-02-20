package com.recipeme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey var id: Int,
    @ColumnInfo(name="aisle") var aisle: String,
    @ColumnInfo(name="amount") var amount: Double,
    @ColumnInfo(name="image") var image: String,
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="unit") var unit: String,
    @ColumnInfo(name="obtained") var obtained: Boolean
)