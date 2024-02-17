package com.recipeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.adapters.GroceryListContentAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.GroceryContentOnItemClick
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class GroceryListContentActivity : AppCompatActivity(), GroceryContentOnItemClick, View.OnClickListener {
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var groceryListDao: GroceryListDao
    lateinit var fridgeDao: FridgeDao
    lateinit var groceryList: GroceryList
    lateinit var ingredientsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list_content)

        var listNameTextView = findViewById<TextView>(R.id.tvGroceryListContentName)
        var imageButtonRefresh = findViewById<ImageButton>(R.id.iBRefreshGroceryContent )
        var db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        var listName = intent.getStringExtra("ListName").toString()
        listNameTextView.text = listName
        ingredients = emptyList<Ingredient>().toMutableList()
        groceryListDao = db.groceryListDao()
        fridgeDao = db.fridgeDao()
        findViewById<Button>(R.id.btnUpdateGroceryListContent).setOnClickListener(this)
        findViewById<Button>(R.id.btnCancelGroceryListContent).setOnClickListener(this)
        GlobalScope.launch{
            this?.let {
                groceryList = groceryListDao.getListFromName(listName)
            }
            Handler(Looper.getMainLooper()).post{
                for(ingredient in groceryList.items){
                //    ingredients.set()
                    ingredients.add(ingredient)
                }
                ingredientsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }


        ingredientsRecyclerView = findViewById<RecyclerView>(R.id.rvGroceryListContentItems)
        ingredientsRecyclerView.adapter = GroceryListContentAdapter(ingredients, this)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onClick(v: View?) {
        if(v != null) {
            Log.d("Test", "View ID " + v.id.toString())
            when (v.id) {

                R.id.btnCancelGroceryListContent -> {
                    finish()
                }
                R.id.btnUpdateGroceryListContent -> {
                    Log.d("Update Clicked", "Update Clicked")
                    var ingredientsToUpdate = emptyList<Ingredient>().toMutableList()
                    var updateIds = emptyList<Int>().toMutableList()
                    for(ingredient in ingredients){
                        if(ingredient.obtained){
                            ingredientsToUpdate.add(ingredient)
                            groceryList.items.remove(ingredient)
                            //updateIds.add(ingredient.id)
                        }
                    }
                    GlobalScope.launch {
                        this?.let {
                            //edit
                            fridgeDao.insertAll(ingredientsToUpdate)

                            groceryListDao.update(groceryList)
                            Handler(Looper.getMainLooper()).post{
                                Log.d("updating",  ingredientsToUpdate.toString())
                                for(ingredient in ingredientsToUpdate){
                                    ingredients.remove(ingredient)
                                }
                                ingredientsRecyclerView.adapter?.notifyDataSetChanged()
                                intent.putExtra("ListName", groceryList.name)
                            }

                        }
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onClickStatus(position: Int) {
        ingredients[position].obtained = !ingredients[position].obtained
        Log.d("Status Test", "Ingredient: " + ingredients[position].name + " Status: " + ingredients[position].obtained + ingredients[position].toString())
        ingredientsRecyclerView.adapter?.notifyDataSetChanged()
    }
}