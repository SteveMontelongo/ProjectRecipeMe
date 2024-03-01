package com.recipeme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey var id: Int,
    @ColumnInfo(name="ingredients") val usedIngredients: List<UsedIngredientsItem?>?,
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="image") var image: String,
    @ColumnInfo(name="instructions") var instructions: Instructions,
    @ColumnInfo(name="saved") var saved: Boolean
)

@Entity
data class Instructions(
    @ColumnInfo(name="steps") val step: List<String>,
    @ColumnInfo(name="number") val number: List<Int>
)
