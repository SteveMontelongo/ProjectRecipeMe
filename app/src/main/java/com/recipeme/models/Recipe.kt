package com.recipeme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Recipe(
    @PrimaryKey var id: Int,
    @ColumnInfo(name="ingredients") val usedIngredients: List<UsedIngredientsItem>,
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="image") var image: String,
    @ColumnInfo(name="instructions") var instructions: MutableList<Instructions>,
    @ColumnInfo(name="saved") var saved: Boolean
)

@Entity
data class Instructions(
    @field:SerializedName("steps")
    @ColumnInfo(name="steps") var steps: MutableList<Steps>,
    @field:SerializedName("name")
    @ColumnInfo(name="name") var name: String
)

@Entity
data class Steps(
//    @field:SerializedName("equipment")
//    @ColumnInfo(name="equipment") val equipment: List<String>,
    @field:SerializedName("ingredients")
    @ColumnInfo(name="ingredients") var ingredients: List<UsedIngredientsItem>,
    @field:SerializedName("number")
    @ColumnInfo(name="number") var number: Int,
    @field:SerializedName("step")
    @ColumnInfo(name="step") var step: String
)

