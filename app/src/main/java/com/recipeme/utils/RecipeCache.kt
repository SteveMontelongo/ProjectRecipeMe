package com.recipeme.utils

import com.recipeme.models.Recipe

object RecipeCache {
    var recipeCache =  emptyList<Recipe>().toMutableList()
    var recipesSaved = emptyList<Recipe>().toMutableList()
}