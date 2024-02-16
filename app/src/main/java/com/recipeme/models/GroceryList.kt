package com.recipeme.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.recipeme.utils.Converters

@Entity
data class GroceryList (
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "time_created") var timeCreated: String,
    @ColumnInfo(name = "ingredients") var items: MutableList<Ingredient>
        ){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}