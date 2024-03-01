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
import com.recipeme.utils.IngredientsData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class GroceryListContentActivity : AppCompatActivity(), GroceryContentOnItemClick, View.OnClickListener {
    private lateinit var _ingredients: MutableList<Ingredient>
    private lateinit var _groceryListDao: GroceryListDao
    private lateinit var _fridgeDao: FridgeDao
    private lateinit var _groceryList: GroceryList
    private lateinit var _ingredientsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list_content)
        var listNameTextView = findViewById<TextView>(R.id.tvGroceryListContentName)
        var db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        var listName = intent.getStringExtra("ListName").toString()
        listNameTextView.text = listName
        _ingredients = emptyList<Ingredient>().toMutableList()
        _groceryListDao = db.groceryListDao()
        _fridgeDao = db.fridgeDao()
        findViewById<Button>(R.id.btnUpdateGroceryListContent).setOnClickListener(this)
        findViewById<Button>(R.id.btnCancelGroceryListContent).setOnClickListener(this)
        GlobalScope.launch{
            this?.let {
                _groceryList = _groceryListDao.getListFromName(listName)
            }
            Handler(Looper.getMainLooper()).post{
                for(ingredient in _groceryList.items){
                    _ingredients.add(ingredient)
                }
                _ingredientsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
        _ingredientsRecyclerView = findViewById(R.id.rvGroceryListContentItems)
        _ingredientsRecyclerView.adapter = GroceryListContentAdapter(_ingredients, this)
        _ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onClick(v: View?) {
        if(v != null) {
            when (v.id) {
                R.id.btnCancelGroceryListContent -> {
                    finish()
                }
                R.id.btnUpdateGroceryListContent -> {
                    var ingredientsToUpdate = emptyList<Ingredient>().toMutableList()
                    for(ingredient in _ingredients){
                        if(ingredient.obtained){
                            ingredientsToUpdate.add(ingredient)
                            _groceryList.items.remove(ingredient)
                        }
                    }
                    GlobalScope.launch {
                        this?.let {
                            _fridgeDao.insertAll(ingredientsToUpdate)

                            _groceryListDao.update(_groceryList)
                            Handler(Looper.getMainLooper()).post{
                                for(ingredient in ingredientsToUpdate){
                                    _ingredients.remove(ingredient)
                                }
                                _ingredientsRecyclerView.adapter?.notifyDataSetChanged()
                                intent.putExtra("ListName", _groceryList.name)
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
        _ingredients[position].obtained = !_ingredients[position].obtained
        _ingredientsRecyclerView.adapter?.notifyDataSetChanged()
    }
}