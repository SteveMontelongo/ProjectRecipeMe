package com.recipeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.adapters.GroceryListContentAdapter
import com.recipeme.adapters.RecipeConfirmationAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.GroceryContentOnItemClick
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import com.recipeme.utils.IngredientsData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecipeConfirmationActivity : AppCompatActivity(), GroceryContentOnItemClick, View.OnClickListener {
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var fridgeDao: FridgeDao
    lateinit var ingredientsRecyclerView: RecyclerView
    lateinit var ids: IntArray
    lateinit var fridgeItems: MutableList<Ingredient>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_confirmation)
        ingredients = emptyList<Ingredient>().toMutableList()
        var db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        fridgeDao = db.fridgeDao()
        ingredientsRecyclerView = findViewById<RecyclerView>(R.id.rvRecipeConfirmation)
        ingredientsRecyclerView.adapter = RecipeConfirmationAdapter(ingredients, this)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ids = intent.getIntArrayExtra("ingredientsUsedIds")!!

        findViewById<Button>(R.id.btnCancelRecipeConfirmation).setOnClickListener(this)
        findViewById<Button>(R.id.btnUpdateRecipeConfirmation).setOnClickListener(this)

        GlobalScope.launch{
            this?.let {
                fridgeItems = fridgeDao.getAll()
            }
            Handler(Looper.getMainLooper()).post{
                var fridgeIds = IntArray(fridgeItems.size)
                var j =0
                for(ingredient in fridgeItems){
                    fridgeIds[j] = ingredient.id
                    j++
                }
                for(id in ids){
                    if(fridgeIds.contains(id)){
                        ingredients.add(Ingredient(id, "null", 0.0, "null", IngredientsData.map.get(id)!!, "", false))
                        Log.d("ingredients", ingredients.toString())
                    }
                }
                ingredientsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onClickStatus(position: Int) {
        ingredients[position].obtained = !ingredients[position].obtained
        Log.d("Status Test", "Ingredient: " + ingredients[position].name + " Status: " + ingredients[position].obtained + ingredients[position].toString())
        ingredientsRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnUpdateRecipeConfirmation ->{
//                    val intent = Intent(this, RecipeConfirmationActivity::class.java)
//                    intent.putExtra("ingredientsUsedIds", filteredIds)
                    GlobalScope.launch{
                        this?.let {
                            for(ingredient in ingredients){
                                if(ingredient.obtained){
                                    fridgeDao.deleteById(ingredient.id)
                                }
                            }

                        }
                        Handler(Looper.getMainLooper()).post{
                            finish()
                        }
                    }

                }

                R.id.btnCancelRecipeConfirmation->{
                    finish()
                }

            }
        }
    }
}