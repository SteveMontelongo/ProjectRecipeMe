package com.recipeme.network

import com.recipeme.models.RecipeResponse
import retrofit2.Call
import retrofit2.http.*
interface ApiService {

    @GET("findByIngredients")
    fun getRecipeByIngredients(
        @Query("apiKey") key:String = ApiConfig.API_KEY,
        @Query("ingredients") ingredients: String,
        @Query("number") number: String = "2"
    ): Call<List<RecipeResponse>>
}