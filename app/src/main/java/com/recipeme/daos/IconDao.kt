package com.recipeme.daos

import androidx.room.*
import com.recipeme.models.Ingredient
import com.recipeme.models.IngredientIcon

@Dao
interface IconDao {
    @Query("SELECT * FROM IngredientIcon")
    fun getAll(): MutableList<IngredientIcon>

    @Query("SELECT * FROM IngredientIcon WHERE id =:id")
    fun getById(id: Int): IngredientIcon

    @Insert
    fun insertAll(icons: MutableList<IngredientIcon>)
    @Insert
    fun insertIcon(icon: IngredientIcon)

    @Update
    fun update(icon: IngredientIcon)

    @Delete
    fun deleteIcon(icon: IngredientIcon)

    @Query("DELETE FROM IngredientIcon WHERE id = :id")
    fun deleteById(id: Int)

}