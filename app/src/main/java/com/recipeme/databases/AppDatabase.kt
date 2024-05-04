package com.recipeme.databases

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.daos.IconDao
import com.recipeme.daos.RecipeDao
import com.recipeme.models.*
import com.recipeme.utils.Converters

@Database(entities = [GroceryList::class, Ingredient::class, Recipe::class, IngredientIcon::class],
    version = 1,
    autoMigrations = [
    ], exportSchema = true)
@TypeConverters(*[Converters::class])

abstract class AppDatabase: RoomDatabase() {

    abstract fun groceryListDao(): GroceryListDao
    abstract fun fridgeDao(): FridgeDao
    abstract fun recipeDao(): RecipeDao
    abstract fun iconDao(): IconDao

}