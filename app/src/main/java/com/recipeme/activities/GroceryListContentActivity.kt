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

class GroceryListContentActivity : AppCompatActivity(), GroceryContentOnItemClick, View.OnClickListener {
    private lateinit var _ingredients: MutableList<Ingredient>
    private lateinit var _groceryListDao: GroceryListDao
    private lateinit var _fridgeDao: FridgeDao
    private lateinit var _groceryList: GroceryList
    private lateinit var _ingredientsRecyclerView: RecyclerView
    private lateinit var _background: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list_content)
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        val listNameTextView = findViewById<TextView>(R.id.tvGroceryListContentName)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        val listName = intent.getStringExtra("ListName").toString()
        listNameTextView.text = listName
        _ingredients = emptyList<Ingredient>().toMutableList()
        _groceryListDao = db.groceryListDao()
        _fridgeDao = db.fridgeDao()
        val updateButton = findViewById<Button>(R.id.btnUpdateGroceryListContent)
        updateButton.setOnClickListener(this)
        updateButton.text = getMsg(1, languageString!!)
        _background = findViewById(R.id.backgroundAppGroceryContent)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)
        findViewById<ImageButton>(R.id.btnCancelGroceryListContent).setOnClickListener(this)
        GlobalScope.launch{
            this.let {
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
        _ingredientsRecyclerView.adapter = GroceryListContentAdapter(_ingredients, this, this)
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
                    var ingredientIdsFromFridge: List<Int>
                    for(ingredient in _ingredients){
                        if(ingredient.custom && ingredient.obtained){
                            _groceryList.items.remove(ingredient)
                            continue
                        }
                        if(ingredient.obtained){
                            ingredientsToUpdate.add(ingredient)
                            _groceryList.items.remove(ingredient)
                            Toast.makeText(this, ingredient.name + " added to fridge.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    GlobalScope.launch {
                        this.let {
                            ingredientIdsFromFridge = _fridgeDao.getAllIds()
                            //clears up duplicates
                            Log.d("GroceryList", "Fridge $ingredientIdsFromFridge")
                            Log.d("GroceryList", "Updating $ingredientsToUpdate")
                            ingredientsToUpdate.removeAll { ingredientIdsFromFridge.contains(it.id) }
//                            var ingredientIndexes = emptyList<Int>().toMutableList()
//                            for((i, ingredient) in ingredientsToUpdate.withIndex()){
//                                if(ingredientIdsFromFridge.contains(ingredient.id)){
//                                    Log.d("GroceryList", "Removed ${ingredient.name}")
//                                    ingredientIndexes.add(i)
//                                    //ingredientsToUpdate.remove(ingredient)
//                                }
//                            }
//                            for(i in ingredientIndexes){
//                                ingredientsToUpdate.removeAt(i)
//                            }
                            Log.d("GroceryList", "Updated List $ingredientsToUpdate")
                            if(ingredientsToUpdate.isNotEmpty()){
                                _fridgeDao.insertAll(ingredientsToUpdate)
                            }
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
        _groceryList.items.clear()
        _groceryList.items.addAll(_ingredients)
        GlobalScope.launch{
            this.let {
                _groceryListDao.update(_groceryList)
                Handler(Looper.getMainLooper()).post{
                    _ingredientsRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.grocery_content_label)
                1 -> getString(R.string.page_update_label)
                else -> getString(R.string.grocery_content_label)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.grocery_content_label_sp)
                1 -> getString(R.string.page_update_label_sp)
                else -> getString(R.string.grocery_content_label_sp)
            }
        }
        return "invalid"
    }

    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
    }
}