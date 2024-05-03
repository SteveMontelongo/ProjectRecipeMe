package com.recipeme.interfaces

interface PaginationInteraction {
    fun incrementPage(page: Int)
    fun decrementPage(page: Int)
    fun pageReset()
}