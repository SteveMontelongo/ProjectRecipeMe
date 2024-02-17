
package com.recipeme.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.recipeme.R
import com.recipeme.models.Ingredient
import com.recipeme.models.RecipeResponse
import com.recipeme.viewmodel.RecipeViewModel

class RecipesFragment : Fragment(), View.OnClickListener {

    lateinit var recipeViewModel: RecipeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        recipeViewModel = RecipeViewModel()
        subscribe()
        Log.d("Subscribe", "Subscribed")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<ImageButton>(R.id.iBtnRefreshRecipes).setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onClick(v: View?) {
        if(v !=null){
            when(v.id){
                R.id.iBtnRefreshRecipes ->{
                    Log.d("Testing Click", "RECIPES REFRESHED")
                    var testList: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
                    val apple: Ingredient = Ingredient("0", 0.0, "", "apples", "tbs", true)
                    val banana: Ingredient = Ingredient("0", 0.0, "", "bananas", "tbs", true)
                    testList.add(apple)
                    testList.add(banana)
                    recipeViewModel.getRecipeData(testList)

                }
            }
        }
    }

    private fun subscribe(){
        recipeViewModel.isLoading.observe(this){isLoading ->
            //if (isLoading) tvResult.text = "Loading..."
        }
        recipeViewModel.isError.observe(this){ isError ->
//            imgCondition.visibility = View.GONE
//            tvResult.text = mainViewModel.errorMessage
        }
        recipeViewModel.recipeData.observe(this){ recipeData ->
            Log.d("Inside Subscribe", "Setting resultText")
            setResultText(recipeData)
        }
    }

    private fun setResultText(recipeData: List<RecipeResponse>) {
        val resultText = StringBuilder("Result:\n")
        Log.d("Response", "Response Received")

//        recipeData.location.let { location ->
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
        Log.d("Response", recipeData[0].title.toString())
//        tvResult.text = resultText
    }
}