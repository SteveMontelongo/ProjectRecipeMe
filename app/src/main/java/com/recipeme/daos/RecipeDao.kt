package com.recipeme.daos

import androidx.room.*
import com.recipeme.models.GroceryList
import com.recipeme.models.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM Recipe WHERE state = 'SAVED'")
    fun getFromCache(): MutableList<Recipe>

    @Query("SELECT id FROM Recipe WHERE state = 'SAVED'")
    fun getIdsFromCache(): MutableList<Int>

//    @Query("SELECT * FROM Recipe WHERE state = 'SAVED'")
//    fun getFromSaved(): MutableList<Recipe>

    @Query("SELECT * FROM Recipe WHERE state = 'FAVORITE'")
    fun getFromFavorite(): MutableList<Recipe>

    @Query("SELECT id FROM Recipe WHERE state = 'FAVORITE'")
    fun getIdsFromFavorite(): MutableList<Int>

    @Query("SELECT * FROM Recipe WHERE id = :id")
    fun getFromId(id: Int): Recipe

    @Insert
    fun insertAll(recipes: MutableList<Recipe>)
    @Insert
    fun insert(recipe: Recipe)

    @Update
    fun update(recipe: Recipe)

    @Delete
    fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM Recipe WHERE id = :id")
    fun deleteById(id: Int)

    @Transaction
    fun updateRecipes(recipes: MutableList<Recipe>){
        for(recipe in recipes){
            update(recipe)
        }
    }
}