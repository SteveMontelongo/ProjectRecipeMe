package com.recipeme.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bumptech.glide.Glide
import com.recipeme.R
import com.recipeme.daos.RecipeDao
import com.recipeme.databases.AppDatabase
import com.recipeme.models.*
import com.recipeme.utils.RecipeCache
import com.recipeme.viewmodel.RecipeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections.copy

class RecipeDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _recipeViewModel: RecipeViewModel
    private lateinit var _ids: IntArray
    private lateinit var _filteredIds: IntArray
    private lateinit var _recipeCache : Recipe
    private lateinit var _recipeDao: RecipeDao
    private var _recipeId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        var db  :AppDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "recipe-me-database").build()
        _recipeViewModel = RecipeViewModel()
        _recipeDao = db.recipeDao()
        var recipeId = intent.getIntExtra("recipeId", 0)
        _recipeId = recipeId
        _ids = intent.getIntArrayExtra("ingredientsUsedIds")!!
        _filteredIds = IntArray(_ids.size)
        _recipeCache = Recipe(0, emptyList(), "", "", mutableListOf<Instructions>() as MutableList<Instructions>, false)
//        for(_recipe in RecipeCache.recipeCache){
//            if(recipeId == _recipe.id){
//                _recipeCache = _recipe
//            }
//        }
        GlobalScope.launch {
            var recipesCache = _recipeDao.getFromCache()
            Handler(Looper.getMainLooper()).post{
                for(_recipe in recipesCache){
                    if(recipeId == _recipe.id){
                        _recipeCache = _recipe
                    }
                }

                if (_recipeCache.id != 0){
                    Log.d("Recipe", "Recipe loaded by Cache")
                    findViewById<TextView>(R.id.tvRecipeNameRecipeDetail).text = _recipeCache.name
                    Glide.with(this@RecipeDetailActivity).load(_recipeCache.image).into(findViewById<ImageView>(R.id.ivRecipeImageRecipeDetail))
                    var ingredientsFormattedString = ""
                    if(_recipeCache.usedIngredients.isNotEmpty() ){
                        var i = 0
                        for(extendedIngredient in _recipeCache.usedIngredients){
                            ingredientsFormattedString += extendedIngredient.name + " QTY : " +  extendedIngredient.amount.toString() + "\n"
                            if(extendedIngredient !=null){
                                if(_ids.contains(extendedIngredient.id!!)){
                                    _filteredIds[i] = extendedIngredient.id
                                    i++
                                }
                            }
                        }
                        findViewById<TextView>(R.id.tvRecipeIngredientsUsedRecipeDetail).text = ingredientsFormattedString

                    }

                }else{
                    Log.d("Recipe", "Recipe loaded by API")
                    _recipeViewModel.getRecipeDataById(recipeId)

                }
                if(_recipeCache.instructions.isEmpty()) {
                    Log.d("Recipe", "Instructions loaded by API")
                    _recipeViewModel.getRecipeInstructionsById(recipeId)
                }else{
                    var resultText = ""
                    for (instruction in _recipeCache.instructions) {
                        for (step in instruction.steps) {
                            resultText += "Step ${step.number}\n\n"
                            if (step.ingredients.isNotEmpty()) {
                                resultText += "Ingredients:\n"

                                for (ingredient in step.ingredients) {
                                    resultText += "${ingredient.name}\n"
                                }
                                resultText += "\n"
                            }
                            //Log.d("Recipe", step.toString())
                            resultText += "${step.step}\n\n"

                        }
                    }
                    findViewById<TextView>(R.id.tvRecipeInstructionsRecipeDetail).text = resultText
                    Log.d("Recipe", "Instructions loaded by Cache")
                }

                subscribe()
            }
        }

        findViewById<Button>(R.id.btnConfirmRecipeDetail).setOnClickListener(this)
        findViewById<ImageButton>(R.id.btnBackRecipeDetail).setOnClickListener(this)
    }

    private fun subscribe(){
        _recipeViewModel.isLoading.observe(this){isLoading ->
            //if (isLoading) tvResult.text = "Loading..."
        }
        _recipeViewModel.isError.observe(this){ isError ->
//            imgCondition.visibility = View.GONE
//            tvResult.text = mainViewModel.errorMessage
        }
        _recipeViewModel.recipeDataById.observe(this){ recipeData ->
            setResultText(recipeData)
        }
        _recipeViewModel.recipeInstructionsById.observe(this){ recipeData ->
            setResultInstructions(recipeData)
        }
    }

    private fun setResultText(recipeData: RecipeResponse) {
        Log.d("Response BY ID", "Response Received")
        findViewById<TextView>(R.id.tvRecipeNameRecipeDetail).text = recipeData.title
        Glide.with(this).load(recipeData.image).into(findViewById<ImageView>(R.id.ivRecipeImageRecipeDetail))
        var ingredientsFormattedString = ""
        var ingredientsUnique = emptySet<UsedIngredientsItem>().toMutableSet()
        if(recipeData.extendedIngredients !=null){
            var i = 0
            ingredientsUnique.clear()
            for(extendedIngredient in recipeData.extendedIngredients){
                if(extendedIngredient!=null){
                    ingredientsUnique.add(extendedIngredient)
                }
            }
            for(extendedIngredient in ingredientsUnique){
                ingredientsFormattedString += extendedIngredient.name + " QTY : " +  extendedIngredient.amount.toString() + "\n"
                if(extendedIngredient !=null){
                    if(_ids.contains(extendedIngredient.id!!)){
                        _filteredIds[i] = extendedIngredient.id
                        i++
                    }
                }
            }
            if(recipeData.extendedIngredients != null && recipeData.extendedIngredients.isNotEmpty()){
                _recipeCache = Recipe(recipeData.id!!, recipeData.extendedIngredients as List<UsedIngredientsItem>, recipeData.title!!, recipeData.image!!, emptyList<Instructions>() as MutableList<Instructions>, false
                )
            }

            findViewById<TextView>(R.id.tvRecipeIngredientsUsedRecipeDetail).text = ingredientsFormattedString
        }

    }

    private fun setResultInstructions(recipeData: List<Instructions>) {
        var resultText = ""
        var recipe: Recipe
        for(instruction in recipeData){
            for(step in instruction.steps) {
                resultText += "Step ${step.number}\n\n"
                if(step.ingredients.isNotEmpty()) {
                    resultText += "Ingredients:\n"

                    for (ingredient in step.ingredients) {
                        resultText += "${ingredient.name}\n"
                    }
                    resultText += "\n"
                }
                //Log.d("Recipe", step.toString())
                resultText += "${step.step}\n\n"

            }
        }
        GlobalScope.launch {
            var instructionsItem: MutableList<Instructions> = mutableListOf<Instructions>() as MutableList<Instructions>
            var stepsItem: MutableList<Steps> = mutableListOf<Steps>() as MutableList<Steps>
            Log.d("Recipe", "Id " + _recipeId)
            recipe  = _recipeDao.getFromId(_recipeId)
            Log.d("Recipe", "Retrieved " + recipe.toString())
            Handler(Looper.getMainLooper()).post{
                var j = 0
                for( instruction in recipeData){
//                    for(step in instruction.steps) {
//                        Log.d("Recipe", "Data: " + step.ingredients.toString() +  step.number.toString() +  step.step)
//                        stepsItem.add(Steps(step.ingredients, step.number, step.step))
//                    }
                    //Log.d("Recipe", "Test " + stepsItem.toString() + instruction.name)
                    instructionsItem.add(Instructions(instruction.steps, instruction.name))
                    //stepsItem.clear()
                }
                Log.d("Recipe", "instructions data " + instructionsItem.toString())
                recipe.instructions.addAll(instructionsItem)
                Log.d("Recipe", "Recipe Data " + recipe.toString())
                GlobalScope.launch {
                    _recipeDao.update(recipe)
                }
            }
        }
        Log.d("Recipe", "ResultText " + resultText)
        findViewById<TextView>(R.id.tvRecipeInstructionsRecipeDetail).text = resultText
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnConfirmRecipeDetail ->{
                    val intent = Intent(this, RecipeConfirmationActivity::class.java)
                    intent.putExtra("ingredientsUsedIds", _filteredIds)
                    resultLauncher.launch(intent)
                }
                R.id.btnBackRecipeDetail ->{
                    finish()
                }
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            finish()
        }
    }

    fun loadRecipeInstructions(){

    }
}

