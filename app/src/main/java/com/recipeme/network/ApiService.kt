package com.recipeme.network

import com.recipeme.models.RecipeResponse
import retrofit2.Call
import retrofit2.http.*
interface ApiService {

    @GET("findByIngredients")
    fun getRecipeByIngredients(
        @Query("apiKey") key:String = ApiConfig.API_KEY,
        @Query("ingredients") ingredients: String,
        @Query("number") number: String = "3"
    ): Call<List<RecipeResponse>>

    @GET("{id}/information")
    fun getRecipeById(
        @Path("id") searchById:String,
        @Query("apiKey") key:String = ApiConfig.API_KEY,
        @Query("includeNutrition") boolean: Boolean = false
    ): Call<RecipeResponse>
}