package com.recipeme.daos

import androidx.room.*
import com.recipeme.models.GroceryList

@Dao
interface GroceryListDao {
    @Query("SELECT * FROM GroceryList")
    fun getAll(): MutableList<GroceryList>

    @Query("SELECT * FROM GroceryList WHERE name= :name")
    fun getListFromName(name: String): GroceryList

    @Query("SELECT name FROM GroceryList")
    fun getAllNames(): MutableList<String>

    @Insert
    fun insertAll(groceryList: GroceryList)

    @Update
    fun update(groceryList: GroceryList)

    @Delete
    fun delete(groceryList: GroceryList)

    @Transaction
    fun updateLists(groceryLists: MutableList<GroceryList>){
        for(groceryList in groceryLists){
            update(groceryList)
        }
    }
}