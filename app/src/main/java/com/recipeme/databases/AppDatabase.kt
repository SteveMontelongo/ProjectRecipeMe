package com.recipeme.databases

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.daos.RecipeDao
import com.recipeme.models.Fridge
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import com.recipeme.models.Recipe
import com.recipeme.utils.Converters

@Database(entities = [GroceryList::class, Ingredient::class, Recipe::class],
    version = 1,
    autoMigrations = [
    ], exportSchema = true)
@TypeConverters(*[Converters::class])

abstract class AppDatabase: RoomDatabase() {

    abstract fun groceryListDao(): GroceryListDao
    abstract fun fridgeDao(): FridgeDao
    abstract fun recipeDao(): RecipeDao

}