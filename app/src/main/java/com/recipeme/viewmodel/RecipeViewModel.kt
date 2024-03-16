package com.recipeme.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.recipeme.models.Ingredient
import com.recipeme.models.Instructions
import com.recipeme.models.RecipeResponse
import com.recipeme.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel : ViewModel() {
    private val _recipeData = MutableLiveData<List<RecipeResponse>>()
    val recipeData: MutableLiveData<List<RecipeResponse>> get() = _recipeData

    private val _recipeDataById = MutableLiveData<RecipeResponse>()
    val recipeDataById: MutableLiveData<RecipeResponse> get() = _recipeDataById

    private val _recipeInstructionsById = MutableLiveData<List<Instructions>>()
    val recipeInstructionsById: MutableLiveData<List<Instructions>> get() = _recipeInstructionsById

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    var errorMessage: String = ""
        private set

    fun getRecipeData(ingredients: MutableList<Ingredient>){

        _isLoading.value = true
        _isError.value = false

        var request: String = ""
        var requestUpdated: String = ""

        for(ingredient in ingredients){
            request += ingredient.name +","
        }
        requestUpdated += request.substring(0, request.length-1)
        Log.d("request String", request)
        Log.d("requestUpdated String", requestUpdated)

        val client = ApiConfig.getApiService().getRecipeByIngredients(ingredients = requestUpdated)

        //Send API request using RETROFIT
        client.enqueue(object : Callback<List<RecipeResponse>> {
            override fun onResponse(
                call: Call<List<RecipeResponse>>,
                response: Response<List<RecipeResponse>>
            ){
                val responseBody = response.body()
                if(!response.isSuccessful || responseBody == null){
                    onError("Data Processing Error")
                    return
                }

                _isLoading.value = false
                Log.d("Inside onResponse", "Response")

                _recipeData.postValue(responseBody!!) //assertion may be editted
            }

            override fun onFailure(call: Call<List<RecipeResponse>>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
            }
        })
    }

    fun getRecipeDataById(id: Int){

        _isLoading.value = true
        _isError.value = false

        Log.d("Get ID" , id.toString())

        val client = ApiConfig.getApiService().getRecipeById(searchById = id.toString())

        //Send API request using RETROFIT
        client.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ){
                val responseBody = response.body()
                if(!response.isSuccessful || responseBody == null){
                    onError("Data Processing Error")
                    return
                }

                _isLoading.value = false
                _recipeDataById.postValue(responseBody!!) //assertion may be editted
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
            }
        })
    }

    fun getRecipeInstructionsById(id: Int){

        _isLoading.value = true
        _isError.value = false

        Log.d("Get ID" , id.toString())

        val client = ApiConfig.getApiService().getRecipeInstructionsById(searchById = id.toString())

        //Send API request using RETROFIT
        client.enqueue(object : Callback<List<Instructions>> {
            override fun onResponse(
                call: Call<List<Instructions>>,
                response: Response<List<Instructions>>
            ){
                val responseBody = response.body()
                if(!response.isSuccessful || responseBody == null){
                    onError("Data Processing Error")
                    return
                }

                _isLoading.value = false
                _recipeInstructionsById.postValue(responseBody!!) //assertion may be editted
            }

            override fun onFailure(call: Call<List<Instructions>>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
            }
        })
    }

    private fun onError(inputMessage: String?) {

        val message = if (inputMessage.isNullOrBlank() or inputMessage.isNullOrEmpty()) "Unknown Error"
        else inputMessage

        errorMessage = StringBuilder("ERROR: ")
            .append("$message some data may not displayed properly").toString()

        _isError.value = true
        _isLoading.value = false
    }

}

