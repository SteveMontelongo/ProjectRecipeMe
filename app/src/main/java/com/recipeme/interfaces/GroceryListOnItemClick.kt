package com.recipeme.interfaces

interface GroceryListOnItemClick {
    fun onClickDelete(int: Int)
    fun onClickEdit(int: Int, name:String)
    fun onClickItem(int: Int, name: String)
}