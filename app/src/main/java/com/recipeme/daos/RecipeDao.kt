package com.recipeme.daos

import androidx.room.*
import com.recipeme.models.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM Recipe WHERE saved = false")
    fun getFromCache(): MutableList<Recipe>

    @Query("SELECT * FROM Recipe WHERE saved = true")
    fun getFromSaved(): MutableList<Recipe>

    @Insert
    fun insertAll(ingredients: MutableList<Recipe>)

    @Update
    fun update(ingredients: Recipe)

    @Delete
    fun deleteIngredient(ingredient: Recipe)

    @Query("DELETE FROM Recipe WHERE id = :id")
    fun deleteById(id: Int)
}