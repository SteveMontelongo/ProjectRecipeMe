package com.recipeme.daos

import androidx.room.*
import com.recipeme.models.Ingredient

@Dao
interface FridgeDao {
    @Query("SELECT * FROM Ingredient")
    fun getAll(): MutableList<Ingredient>

    @Insert
    fun insertAll(ingredients: MutableList<Ingredient>)

    @Update
    fun update(ingredients: Ingredient)

    @Delete
    fun deleteIngredient(ingredient: Ingredient)

}