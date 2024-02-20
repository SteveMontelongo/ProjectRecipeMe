package com.recipeme.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.recipeme.R
import com.recipeme.models.Ingredient
import com.recipeme.models.RecipeResponse
import com.recipeme.models.UsedIngredientsItem
import com.recipeme.viewmodel.RecipeViewModel

class RecipeDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var recipeViewModel: RecipeViewModel
    lateinit var ids: IntArray
    lateinit var filteredIds: IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        var recipeId = intent.getIntExtra("recipeId", 0)
        ids = intent.getIntArrayExtra("ingredientsUsedIds")!!
        filteredIds = IntArray(ids.size)
        //Log.d("ids", ids!![0].toString())
        Log.d("recipeId", recipeId.toString())
        recipeViewModel = RecipeViewModel()
        recipeViewModel.getRecipeDataById(recipeId)
        subscribe()

        var btnConfirm = findViewById<Button>(R.id.btnConfirmRecipeDetail).setOnClickListener(this)
//        var recipeImageUrl = intent.getStringExtra("RecipeImage").toString()
//        Log.d("recipeImage", recipeImageUrl)
//        var recipeUsedIngredients = intent.get("RecipeIngredientsUsed", object : object : TypeToken<List<T>>(){}).
//        Log.d("recipeUsedIngredients", recipeUsedIngredients)
//        var recipeMissingIngredients = intent.getStringExtra("RecipeMissingIngredients").toString()
//        Log.d("recipeMissingIngredients", recipeMissingIngredients)
//        var usedIngredientsList: List<UsedIngredientsItem> = Gson().fromJson(usedIngredientsJson, object : TypeToken<List<UsedIngredientsItem>>(){}.type)
//        Log.d("recipeMissingIngredients", usedIngredientsList.toString())


//        findViewById<TextView>(R.id.tvRecipeNameRecipeDetail).text = recipeName
//        Glide.with(this).load(recipeImageUrl).into(findViewById<ImageView>(R.id.ivRecipeImageRecipeDetail))
//        findViewById<TextView>(R.id.tvRecipeIngredientsUsedRecipeDetail).text = recipeUsedIngredients
//        findViewById<TextView>(R.id.tvRecipeMissingIngredientsRecipeDetail).text = recipeMissingIngredients



    }

    private fun subscribe(){
        recipeViewModel.isLoading.observe(this){isLoading ->
            //if (isLoading) tvResult.text = "Loading..."
        }
        recipeViewModel.isError.observe(this){ isError ->
//            imgCondition.visibility = View.GONE
//            tvResult.text = mainViewModel.errorMessage
        }
        recipeViewModel.recipeDataById.observe(this){ recipeData ->
            Log.d("Inside Subscribe", "Setting resultText")
            setResultText(recipeData)
        }
    }

    private fun setResultText(recipeData: RecipeResponse) {
        //val resultText = StringBuilder("Result:\n")
        Log.d("Response BY ID", "Response Received")
//        for(recipe in recipeData){
//            recipes.add(recipe)
//        }
////        recipeData.location.let { location ->
//            resultText.append("Name: ${location?.name}\n")
//            resultText.append("Region: ${location?.region}\n")
//            resultText.append("Country: ${location?.country}\n")
//            resultText.append("Timezone ID: ${location?.tzId}\n")
//            resultText.append("Local Time: ${location?.localtime}\n")
//        }
//
//        recipeData.current.let { current ->
//            current?.condition.let { condition ->
//                resultText.append("Condition: ${condition?.text}\n")
//            }
//            resultText.append("Celcius: ${current?.tempC}\n")
//            resultText.append("Fahrenheit: ${current?.tempF}\n")
//        }

        findViewById<TextView>(R.id.tvRecipeNameRecipeDetail).text = recipeData.title
        Glide.with(this).load(recipeData.image).into(findViewById<ImageView>(R.id.ivRecipeImageRecipeDetail))
        var inregedientsFormattedString = ""
        if(recipeData.extenedIngredients !=null){
            var i =0
            for(extendedIngredient in recipeData.extenedIngredients){
                inregedientsFormattedString += "* " + extendedIngredient!!.amount.toString() + " - " + extendedIngredient.name + "\n"

                if(extendedIngredient !=null){
                    if(ids.contains(extendedIngredient.id!!)){
                        filteredIds[i] = extendedIngredient.id
                        i++
                    }
                }
            }
        }

        findViewById<TextView>(R.id.tvRecipeIngredientsUsedRecipeDetail).text = inregedientsFormattedString
        //findViewById<TextView>(R.id.tvRecipeMissingIngredientsRecipeDetail).text = recipeData.missedIngredients.toString()
        Log.d("Response BY ID", recipeData.title.toString())
//        tvResult.text = resultText
//        recyclerView.adapter?.notifyDataSetChanged()
//        Log.d("Recipes list 2", recipes.toString())
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnConfirmRecipeDetail ->{
                    val intent = Intent(this, RecipeConfirmationActivity::class.java)
                    intent.putExtra("ingredientsUsedIds", filteredIds)

                    resultLauncher.launch(intent)
                }

            }
        }
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
//            val data: Intent? = result.data
//            val dataListName = data?.getStringExtra("ListName")
//            val dataListDate = data?.getStringExtra("ListDate")
//            Log.d("Test", dataListName.toString())
//            Log.d("Date" , dataListDate.toString())
//            grocerylists.add(
//                GroceryList(dataListName.toString(), dataListDate.toString(),
//                emptyList<Ingredient>().toMutableList())
//            )
//            val indexInsert = grocerylists.size
//            recyclerView.adapter?.notifyItemInserted(indexInsert)
            //recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}

