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
import com.recipeme.R
import com.recipeme.models.RecipeResponse
import com.recipeme.viewmodel.RecipeViewModel

class RecipeDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var _recipeViewModel: RecipeViewModel
    private lateinit var _ids: IntArray
    private lateinit var _filteredIds: IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        var recipeId = intent.getIntExtra("recipeId", 0)
        _ids = intent.getIntArrayExtra("ingredientsUsedIds")!!
        _filteredIds = IntArray(_ids.size)
        _recipeViewModel = RecipeViewModel()
        _recipeViewModel.getRecipeDataById(recipeId)
        subscribe()
        findViewById<Button>(R.id.btnConfirmRecipeDetail).setOnClickListener(this)

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
    }

    private fun setResultText(recipeData: RecipeResponse) {
        Log.d("Response BY ID", "Response Received")

        findViewById<TextView>(R.id.tvRecipeNameRecipeDetail).text = recipeData.title
        Glide.with(this).load(recipeData.image).into(findViewById<ImageView>(R.id.ivRecipeImageRecipeDetail))
        var ingredientsFormattedString = ""
        if(recipeData.extendedIngredients !=null){
            var i =0
            for(extendedIngredient in recipeData.extendedIngredients){
                ingredientsFormattedString += "* " + extendedIngredient!!.amount.toString() + " - " + extendedIngredient.name + "\n"

                if(extendedIngredient !=null){
                    if(_ids.contains(extendedIngredient.id!!)){
                        _filteredIds[i] = extendedIngredient.id
                        i++
                    }
                }
            }
        }
        findViewById<TextView>(R.id.tvRecipeIngredientsUsedRecipeDetail).text = ingredientsFormattedString
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnConfirmRecipeDetail ->{
                    val intent = Intent(this, RecipeConfirmationActivity::class.java)
                    intent.putExtra("ingredientsUsedIds", _filteredIds)
                    resultLauncher.launch(intent)
                }
            }
        }
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){

        }
    }
}

