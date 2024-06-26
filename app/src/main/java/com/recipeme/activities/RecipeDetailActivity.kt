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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import com.bumptech.glide.Glide
import com.recipeme.R
import com.recipeme.daos.IconDao
import com.recipeme.daos.RecipeDao
import com.recipeme.databases.AppDatabase
import com.recipeme.models.*
import com.recipeme.viewmodel.RecipeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _recipeViewModel: RecipeViewModel
    private lateinit var _ids: IntArray
    private lateinit var _filteredIds: IntArray
    private lateinit var _filteredStrings: Array<String>
    private lateinit var _filteredImageStrings: Array<String>
    private lateinit var _recipeCache : Recipe
    private lateinit var _recipeDao: RecipeDao
    private lateinit var _iconDao: IconDao
    private var _isFavorite: Boolean = false
    private var _recipeId: Int = 0
    private lateinit var _favoriteButton: ImageButton
    private lateinit var _background: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        val db  :AppDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "recipe-me-database").build()
        _recipeViewModel = RecipeViewModel()
        _recipeDao = db.recipeDao()
        _iconDao = db.iconDao()

        val recipeId = intent.getIntExtra("recipeId", 0)
        _recipeId = recipeId
        _ids = intent.getIntArrayExtra("ingredientsUsedIds")!!
        _filteredIds = IntArray(_ids.size)
        _filteredImageStrings = Array(_ids.size){""}
        _filteredStrings = Array(_ids.size){""}
        _recipeCache = Recipe(0, emptyList(), "", "", mutableListOf(), "UNSAVED")
//        for(_recipe in RecipeCache.recipeCache){
//            if(recipeId == _recipe.id){
//                _recipeCache = _recipe
//            }
//        }
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        _background = findViewById(R.id.backgroundAppRecipeDetail)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)

        val ingredientsLabel = findViewById<TextView>(R.id.tvRecipeIngredientsUsedLabelRecipeDetail)
        ingredientsLabel.text = getMsg(0, languageString!!)
        val stepsLabel = findViewById<TextView>(R.id.tvRecipeInstructionsLabelRecipeDetail)
        stepsLabel.text = getMsg(1, languageString)
        _favoriteButton = findViewById(R.id.btnFavoriteRecipeDetail)
        _favoriteButton.setOnClickListener(this)
        GlobalScope.launch {
            val recipesCache = _recipeDao.getFromCache()
            val recipesFavorite = _recipeDao.getFromFavorite()
            Handler(Looper.getMainLooper()).post{
                for(_recipe in recipesCache){
                    if(recipeId == _recipe.id){
                        _recipeCache = _recipe
                    }
                }
                for(_recipe in recipesFavorite){
                    if(recipeId == _recipe.id){
                        _recipeCache = _recipe
                        _isFavorite = true
                    }
                }
                Log.d("Recipe Information", _recipeCache.name + _recipeCache.usedIngredients.toString())
                isFavorite(_isFavorite, _favoriteButton)
                if (_recipeCache.id != 0){
                    Log.d("Recipe", "Recipe loaded by Cache")
                    findViewById<TextView>(R.id.tvRecipeNameRecipeDetail).text = _recipeCache.name
                    Glide.with(this@RecipeDetailActivity).load(_recipeCache.image).into(findViewById(R.id.ivRecipeImageRecipeDetail))
                    var ingredientsFormattedString = ""
                    if(_recipeCache.usedIngredients.isNotEmpty() ){
                        var i = 0
                        for(extendedIngredient in _recipeCache.usedIngredients){
                            ingredientsFormattedString += extendedIngredient.name + " QTY : " +  extendedIngredient.amount.toString() + "\n"
                            if(extendedIngredient !=null){
                                if(_ids.contains(extendedIngredient.id!!)){
                                    _filteredIds[i] = extendedIngredient.id
                                    _filteredStrings[i] = extendedIngredient.name.toString()
                                    _filteredImageStrings[i] = extendedIngredient.image.toString()
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
                    if(resultText == ""){
                        resultText += "Instructions not available."
                    }
                    findViewById<TextView>(R.id.tvRecipeInstructionsRecipeDetail).text = resultText
                    Log.d("Recipe", "Instructions loaded by Cache")
                }
                subscribe()
            }
        }

        val confirmButton = findViewById<Button>(R.id.btnConfirmRecipeDetail)
        confirmButton.setOnClickListener(this)
        confirmButton.text = getMsg(2, languageString)
        val cancelButton = findViewById<ImageButton>(R.id.btnBackRecipeDetail)
        cancelButton.setOnClickListener(this)
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
        Glide.with(this).load(recipeData.image).into(findViewById(R.id.ivRecipeImageRecipeDetail))
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
                        _filteredStrings[i] = extendedIngredient.name.toString()
                        _filteredImageStrings[i] = extendedIngredient.image.toString()
                        Log.d("RecipeDetail Ingredient", "Index $i -" + extendedIngredient.id.toString() +" - "+ extendedIngredient.name +" - "+ extendedIngredient.image)
                        i++
                    }
                }
            }
            if(recipeData.extendedIngredients != null && recipeData.extendedIngredients.isNotEmpty()){
                _recipeCache = Recipe(recipeData.id!!, recipeData.extendedIngredients as List<UsedIngredientsItem>, recipeData.title!!, recipeData.image!!, emptyList<Instructions>() as MutableList<Instructions>, "UNSAVED"
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
            val instructionsItem: MutableList<Instructions> = mutableListOf()
            Log.d("Recipe", "Id " + _recipeId)
            recipe  = _recipeDao.getFromId(_recipeId)
            Log.d("Recipe", "Retrieved " + recipe.toString())
            Handler(Looper.getMainLooper()).post{
                for( instruction in recipeData){
                    instructionsItem.add(Instructions(instruction.steps, instruction.name))
                }
                Log.d("Recipe", "instructions data " + instructionsItem.toString())
                recipe.instructions.addAll(instructionsItem)
                Log.d("Recipe", "Recipe Data " + recipe.toString())
                GlobalScope.launch {
                    _recipeDao.update(recipe)
                }
            }
        }
        Log.d("Recipe", "ResultText $resultText")
        if(resultText == ""){
            resultText += "Instructions not available."
        }
        findViewById<TextView>(R.id.tvRecipeInstructionsRecipeDetail).text = resultText
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnConfirmRecipeDetail ->{
                    GlobalScope.launch {
                        var iconNamesUpdated = emptyList<String>().toMutableList()
                        var icons = _iconDao.getAll()
                        var iconsIds = emptyList<Int>().toMutableList()
                        for(icon in icons){
                            iconsIds.add(icon.id)
                        }
                        if(_filteredIds.isNotEmpty()){
                            for((i, ingredientId) in _filteredIds.withIndex()){
                                if(iconsIds.isNotEmpty()) {
                                    if (!iconsIds.contains(ingredientId)) {
                                        Log.d(
                                            "RecipeDetail Data",
                                            "ID - $ingredientId - " + _filteredStrings[i] + " - " + _filteredImageStrings
                                        )
                                        val updateIcon = IngredientIcon(
                                            ingredientId,
                                            _filteredStrings[i],
                                            _filteredImageStrings[i]
                                        )
                                        _iconDao.insertIcon(updateIcon)
                                        iconNamesUpdated.add(_filteredStrings[i])
                                    }
                                }else{
                                    if(ingredientId != 0) {
                                        Log.d(
                                            "RecipeDetail Data",
                                            "ID - $ingredientId - " + _filteredStrings[i] + " - " + _filteredImageStrings
                                        )
                                        val updateIcon = IngredientIcon(
                                            ingredientId,
                                            _filteredStrings[i],
                                            _filteredImageStrings[i]
                                        )
                                        _iconDao.insertIcon(updateIcon)
                                        iconNamesUpdated.add(_filteredStrings[i])
                                    }
                                }
                            }
                        }
                        Handler(Looper.getMainLooper()).post{
                            for(item in iconNamesUpdated){
                                Toast.makeText(applicationContext,"Congrats! "+ item + " icon earned!", Toast.LENGTH_LONG).show()
                            }
                    }
                    }
                    val intent = Intent(this, RecipeConfirmationActivity::class.java)
                    intent.putExtra("ingredientsUsedIds", _filteredIds)
                    intent.putExtra("ingredientUsedNames", _filteredStrings)
                    resultLauncher.launch(intent)
                }
                R.id.btnBackRecipeDetail ->{
                    finish()
                }
                R.id.btnFavoriteRecipeDetail->{
                    toggleFavorite(_isFavorite, _favoriteButton)
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


    private fun isFavorite(isFavorite: Boolean, favoriteButton : ImageButton){
        if(isFavorite){
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_filled)
        }else{
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_unfilled)
        }
    }

    private fun toggleFavorite(isFavorite: Boolean, button: ImageButton){
        if(isFavorite){
            _recipeCache.state = "SAVED"

        }else{
            _recipeCache.state = "FAVORITE"
        }

        GlobalScope.launch {
            _recipeDao.update(_recipeCache)
            Handler(Looper.getMainLooper()).post {
                Log.d("RecipeFragment", "Recipe Toggle Updated")
            }
        }
        Log.d("Favorite - before", _isFavorite.toString())
        _isFavorite = !_isFavorite
        Log.d("Favorite - after", _isFavorite.toString())
        isFavorite(_isFavorite, button)
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.recipe_detail_ingredients_label)
                1 -> getString(R.string.recipe_detail_steps_label)
                2 -> getString(R.string.recipe_detail_steps_label)
                else -> getString(R.string.grocery_edit_input_warning_1)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.recipe_detail_ingredients_label_sp)
                1 -> getString(R.string.recipe_detail_steps_label_sp)
                2 -> getString(R.string.recipe_detail_steps_label_sp)
                else -> getString(R.string.recipe_confirmation_page_label_sp)
            }
        }
        return "invalid"
    }
    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
    }

}

