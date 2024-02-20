
package com.recipeme.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavType
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.recipeme.R
import com.recipeme.activities.PopupGroceryListsList
import com.recipeme.activities.RecipeDetailActivity
import com.recipeme.adapters.RecipesAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.RecipeOnClickItem
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import com.recipeme.models.RecipeResponse
import com.recipeme.viewmodel.RecipeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecipesFragment : Fragment(), View.OnClickListener, RecipeOnClickItem {

    lateinit var recipeViewModel: RecipeViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var recipes: MutableList<RecipeResponse>
    lateinit var fridgeDao: FridgeDao
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var ids: IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        var db : AppDatabase
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "recipe-me-database"
        ).build()
        fridgeDao = db.fridgeDao()
        ingredients = emptyList<Ingredient>().toMutableList()

        recipeViewModel = RecipeViewModel()
        subscribe()
        Log.d("Subscribe", "Subscribed")


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recipes = emptyList<RecipeResponse>().toMutableList()
        view.findViewById<ImageButton>(R.id.iBtnRefreshRecipes).setOnClickListener(this)
        recyclerView = view.findViewById<RecyclerView>(R.id.rvRecipes)
        recyclerView.adapter = RecipesAdapter(recipes, this, requireContext())
        recyclerView.layoutManager= LinearLayoutManager(this.context)

        GlobalScope.launch {
            var ingredientsToUpdate: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
            ingredientsToUpdate.addAll(fridgeDao.getAll())
            ids = IntArray(ingredientsToUpdate.size)
            Handler(Looper.getMainLooper()).post{
                Log.d("Fridge Loading", ingredientsToUpdate.toString())
                ingredients.addAll(ingredientsToUpdate)
                var i = 0;
                for(ingredient in ingredients){
                    ids[i] = ingredient.id
                    i++
                }
                Log.d("All Ingredients", "All ingredients " + ingredients.toString())
            }
        }
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
//                    var testList: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
//                    val apple: Ingredient = Ingredient(0,"", 0.0, "", "apples", "QTY", true)
//                    val banana: Ingredient = Ingredient(0,"", 0.0,"", "bananas", "QTY", true)
//                    testList.add(apple)
//                    testList.add(banana)
                    recipeViewModel.getRecipeData(ingredients)
                    recyclerView.adapter?.notifyDataSetChanged()
                    Log.d("Recipes list", recipes.toString())

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
        //val resultText = StringBuilder("Result:\n")
        Log.d("Response", "Response Received")
        for(recipe in recipeData){
            recipes.add(recipe)
        }
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
        recyclerView.adapter?.notifyDataSetChanged()
        Log.d("Recipes list 2", recipes.toString())
    }

    override fun onClickItem(position: Int) {
        Log.d("Recipe clicked", recipes[position].toString())
        val intent = Intent(this.context, RecipeDetailActivity::class.java)

        intent.putExtra("recipeId", recipes[position].id)
        intent.putExtra("ingredientsUsedIds", ids)

        resultLauncher.launch(intent)
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