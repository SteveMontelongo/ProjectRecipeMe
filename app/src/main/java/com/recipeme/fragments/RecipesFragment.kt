
package com.recipeme.fragments

import android.app.Activity
import android.content.Context
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.activities.MainActivity
import com.recipeme.activities.RecipeDetailActivity
import com.recipeme.adapters.RecipesAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.RecipeDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.MainFragmentInteraction
import com.recipeme.interfaces.PaginationInteraction
import com.recipeme.interfaces.RecipeOnClickItem
import com.recipeme.models.*
import com.recipeme.utils.RecipeCache
import com.recipeme.viewmodel.RecipeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

enum class RecipeState{SEARCH, SAVED, FAVORITE}

class RecipesFragment : Fragment(), View.OnClickListener, RecipeOnClickItem, MainFragmentInteraction, PaginationInteraction {
    private lateinit var _recipeViewModel: RecipeViewModel
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _recipes: MutableList<Recipe>
    private lateinit var _fridgeDao: FridgeDao
    private lateinit var _ingredients: MutableList<Ingredient>
    private lateinit var _recipesDao: RecipeDao
    private lateinit var _ids: IntArray
    private lateinit var _idsFavorite: IntArray
    private lateinit var _previousPageStack: Stack<Recipe>
    private var _isContentEmpty = false
    private var _page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        var db : AppDatabase = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "recipe-me-database"
        ).build()
        _fridgeDao = db.fridgeDao()
        _recipesDao = db.recipeDao()
        _ingredients = emptyList<Ingredient>().toMutableList()
        _previousPageStack = Stack()
        _recipeViewModel = RecipeViewModel()
        subscribe()

        _recipes = emptyList<Recipe>().toMutableList()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("RecipeFragment", "OnAttached")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _recyclerView = view.findViewById<RecyclerView>(R.id.rvRecipes)
        _recyclerView.adapter = RecipesAdapter(_recipes, this, requireContext())
        _recyclerView.layoutManager= LinearLayoutManager(requireContext())
        loadRecipeData("SAVED")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onClick(v: View?) {

    }

    private fun subscribe(){
        _recipeViewModel.isLoading.observe(this){ isLoading ->
            //if (isLoading) tvResult.text = "Loading..."
        }
        _recipeViewModel.isError.observe(this){ isError ->
//            imgCondition.visibility = View.GONE
//            tvResult.text = mainViewModel.errorMessage
        }
        _recipeViewModel.recipeData.observe(this){ recipeData ->
            setResultText(recipeData)
        }
    }

    private fun setResultText(recipeData: List<RecipeResponse>) {
        _recipes.clear()
        _isContentEmpty = if(recipeData.isEmpty()){
            Log.d("Response", "EmptyResponse")
            var main = activity as MainActivity
            main.pageForwardDisable()
            true
        }else{
            false
        }
        for(recipe in recipeData){
            if(recipe.usedIngredients != null){
                    var recipeItem = Recipe(
                        recipe.id!!,
                        recipe.usedIngredients as List<UsedIngredientsItem>,
                        recipe.title!!,
                        recipe.image!!,
                        mutableListOf<Instructions>() as
                                MutableList<Instructions>,
                        "SAVED"
                    )
//                _recipes.add(recipeItem)
//                if(!RecipeCache.recipeCache.contains(recipeItem)){
//                    RecipeCache.recipeCache.add(recipeItem)
//                }
                    _recipes.add(recipeItem)

            }

        }
        GlobalScope.launch {
            var insertRecipes : MutableList<Recipe> = mutableListOf()
            var updateRecipes: MutableList<Recipe> = mutableListOf()
            var allIds: MutableList<Int> = mutableListOf()
            allIds.addAll(_recipesDao.getIdsFromCache())
            var favIds = _recipesDao.getIdsFromFavorite()
            allIds.addAll(favIds)
            for(id in allIds){
                Log.d("IDS", id.toString())
            }
            for(recipe in _recipes){
                if(allIds.contains(recipe.id)){
                    if(!favIds.contains(recipe.id)){
                        updateRecipes.add(recipe)
                        Log.d("UPDATE", recipe.id.toString())
                    }
                }else{
                    insertRecipes.add(recipe)
                    Log.d("INSERT", recipe.id.toString())
                }
            }

            _recipesDao.insertAll(insertRecipes)
            _recipesDao.updateRecipes(updateRecipes)
            Handler(Looper.getMainLooper()).post{
                for(recipe in _recipes){
                    Log.d("Recipe", "Recipe" +  recipe.id.toString() + "-" + recipe.name + " updated to database.")
                }
            }
        }
        Log.d("Recipe", "Data pulled from api.")
        _recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onClickItem(position: Int) {
        Log.d("Recipe clicked", _recipes[position].toString())
        val intent = Intent(this.context, RecipeDetailActivity::class.java)
        intent.putExtra("recipeId", _recipes[position].id)
        intent.putExtra("ingredientsUsedIds", _ids)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){

        }
    }

    override fun refreshClickFragment(data: String) {
        val main = activity as MainActivity
        main.pageForwardEnable()
        loadRecipeData("SEARCH")
    }


    override fun addClickFragment(data: String) {
        TODO("Not yet implemented")
    }

    override fun favoriteClickFragment(data: String) {
        Toast.makeText(this.context, "Favorite", Toast.LENGTH_SHORT).show()
        loadRecipeData("FAVORITE")
    }

    private fun errorMsg(msg: String, isVisible: Boolean){
        var msgError = view?.findViewById<TextView>(R.id.tvErrorMsgRecipes)
        when(isVisible){
            true ->{
                msgError?.visibility = android.view.View.VISIBLE
                msgError?.text = msg
            }
            false ->{
                msgError?.text = ""
                msgError?.visibility = android.view.View.INVISIBLE
            }
        }
    }

    private fun loadRecipeData(state: String){
        var ingredientsFromFridge: MutableList<Ingredient> = emptyList<Ingredient>().toMutableList()
        var recipesToUpdate: MutableList<Recipe> = emptyList<Recipe>().toMutableList()
        ingredientsFromFridge.clear()
        _ingredients.clear()
        when(state) {
            "SEARCH"-> {
                GlobalScope.launch {
                    ingredientsFromFridge.addAll(_fridgeDao.getAll())
                    Handler(Looper.getMainLooper()).post {
                        _ids = IntArray(ingredientsFromFridge.size)
                        _ingredients.addAll(ingredientsFromFridge)
                        for ((i, ingredient) in _ingredients.withIndex()) {
                            _ids[i] = ingredient.id
                        }
                        if (ingredientsFromFridge.isNotEmpty()) {
                            errorMsg(resources.getText(R.string.error_msg_empty_fridge).toString(), false)
                            _recipeViewModel.getRecipeData(ingredientsFromFridge, (_page - 1) * 10)
                        } else {
                            errorMsg(resources.getText(R.string.error_msg_empty_fridge).toString(),true)
                            Log.d("Recipe", "No ingredients found in the fridge.")
                        }
                    }
                }
            }
            "SAVED"-> {
                GlobalScope.launch {
                    ingredientsFromFridge.addAll(_fridgeDao.getAll())
                    recipesToUpdate.addAll(_recipesDao.getFromCache())
                    Handler(Looper.getMainLooper()).post {
                        _ids = IntArray(ingredientsFromFridge.size)
                        _ingredients.addAll(ingredientsFromFridge)
                        for ((i, ingredient) in _ingredients.withIndex()) {
                            _ids[i] = ingredient.id
                        }
                        if (recipesToUpdate.isNotEmpty()) {
                            errorMsg(resources.getText(R.string.error_msg_empty_fridge).toString(), false)
                            Log.d("Recipe", "Data pulled from cache.")
                            _recipes.clear()
                            _recipes.addAll(recipesToUpdate)
                            _recyclerView.adapter?.notifyDataSetChanged()
                        } else {
                            if (ingredientsFromFridge.isNotEmpty()) {
                                errorMsg(resources.getText(R.string.error_msg_empty_fridge).toString(),false)
                                _recipeViewModel.getRecipeData(
                                    ingredientsFromFridge,
                                    (_page - 1) * 10
                                )
                            } else {
                                errorMsg(resources.getText(R.string.error_msg_empty_fridge).toString(),true)
                                Log.d("Recipe", "No ingredients found in the fridge.")
                            }
                        }
                    }
                }
            }
            "FAVORITE"-> {
                GlobalScope.launch {
                    ingredientsFromFridge.addAll(_fridgeDao.getAll())
                    recipesToUpdate.addAll(_recipesDao.getFromFavorite())
                    Handler(Looper.getMainLooper()).post {
                        _ids = IntArray(ingredientsFromFridge.size)
                        _ingredients.addAll(ingredientsFromFridge)
                        for ((i, ingredient) in _ingredients.withIndex()) {
                            _ids[i] = ingredient.id
                        }
                        if (recipesToUpdate.isNotEmpty()) {
                            errorMsg(resources.getText(R.string.error_msg_empty_favorite).toString(),false)
                            Log.d("Recipe", "Data pulled from favorite.")
                            for(recipe in recipesToUpdate){
                                Log.d("RecipeFavorite", recipe.name)
                            }
                            _recipes.clear()
                            _recipes.addAll(recipesToUpdate)
                            _recyclerView.adapter?.notifyDataSetChanged()
                        } else {
                            /*if (ingredientsFromFridge.isNotEmpty()) {
                                isFridgeEmptyMsg(false)
                                _recipeViewModel.getRecipeData(
                                    ingredientsFromFridge,
                                    (_page - 1) * 10
                                )
                            } else {*/
                                errorMsg(resources.getText(R.string.error_msg_empty_favorite).toString(),true)
                                Log.d("Recipe", "No recipes were favorited.")

                        }
                    }
                }
            }
        }
    }

    override fun incrementPage(page: Int) {
        Log.d("Increment", "Increment Page")
        _page = page
        addToPreviousPageStack(_recipes)
        loadRecipeData("SEARCH")
    }

    override fun decrementPage(page: Int) {
        val main = activity as MainActivity
        Log.d("Decrement", "Decrement Page")
        _page = page
        main.pageForwardEnable()
        _recipes.clear()
        if(_previousPageStack.isNotEmpty()){
            Log.d("Recipe Fragment", "Loaded from previous stack")
            _recipes.clear()
            _recipes.addAll(retrieveFromPreviousPageStack())
            Log.d("Recipe Fragment", _recipes.toString())
            _recyclerView.adapter?.notifyDataSetChanged()
        }
        //loadRecipeData(true)
    }

    fun isForwardPageDisabled(): Boolean{
        Log.d("Recipes Fragment Test", _isContentEmpty.toString())
        return _isContentEmpty
    }

    override fun pageReset() {
        _page = 1
    }

    private fun addToPreviousPageStack(recipes: MutableList<Recipe>){
        for(recipe in recipes){
            _previousPageStack.add(recipe)
            Log.d("AddingToStack", recipe.id.toString())
        }
        Log.d("RecipeFragment", _previousPageStack.toString())
    }

    private fun retrieveFromPreviousPageStack(): MutableList<Recipe>{
        var recipesToAdd = emptyList<Recipe>().toMutableList()
        while(_previousPageStack.isNotEmpty() && recipesToAdd.size < 10){
            recipesToAdd.add(_previousPageStack.pop())
            Log.d("RetreivingFromStack", recipesToAdd[recipesToAdd.size - 1].toString())
        }
        if(recipesToAdd.isNotEmpty()){
            Log.d("Not Empty", " ")
            return recipesToAdd.reversed() as MutableList<Recipe>
        }
        val main = activity as MainActivity
        main.pageForwardDisable()
        _isContentEmpty = true
        Log.d("Recipes Fragment Internal Test", _isContentEmpty.toString())
        Log.d("Empty", " ")
        return emptyList<Recipe>().toMutableList()
    }
}