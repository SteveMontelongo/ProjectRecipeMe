package com.recipeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.adapters.GroceryListContentAdapter
import com.recipeme.adapters.RecipeConfirmationAdapter
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.daos.IconDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.GroceryContentOnItemClick
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import com.recipeme.utils.IngredientsData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecipeConfirmationActivity : AppCompatActivity(), GroceryContentOnItemClick, View.OnClickListener {
    private lateinit var _ingredients: MutableList<Ingredient>
    private lateinit var _fridgeDao: FridgeDao
    private lateinit var _ingredientsRecyclerView: RecyclerView
    private lateinit var _ids: IntArray
    private lateinit var _names: Array<String>
    private lateinit var _fridgeItems: MutableList<Ingredient>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_confirmation)
        _ingredients = emptyList<Ingredient>().toMutableList()
        var db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        _fridgeDao = db.fridgeDao()
        _ingredientsRecyclerView = findViewById<RecyclerView>(R.id.rvRecipeConfirmation)
        _ingredientsRecyclerView.adapter = RecipeConfirmationAdapter(_ingredients, this)
        _ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        _ids = intent.getIntArrayExtra("ingredientsUsedIds")!!
        _names = intent.getStringArrayExtra("ingredientUsedNames")!!
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")

        var labelText = findViewById<TextView>(R.id.tvLabelRecipeConfirmation)
        labelText.text = getMsg(0, languageString!!)
        var cancelButton = findViewById<Button>(R.id.btnCancelRecipeConfirmation)
        cancelButton.setOnClickListener(this)
        cancelButton.text = getMsg(1, languageString!!)
        var updateButton = findViewById<Button>(R.id.btnUpdateRecipeConfirmation)
        updateButton.setOnClickListener(this)
        updateButton.text == getMsg(2, languageString!!)

        GlobalScope.launch{
            this?.let {
                _fridgeItems = _fridgeDao.getAll()
            }
            Handler(Looper.getMainLooper()).post{
                var fridgeIds = IntArray(_fridgeItems.size)
                for((j, ingredient) in _fridgeItems.withIndex()){
                    fridgeIds[j] = ingredient.id
                    Log.d("Fridge Items", ingredient.name + " - " + ingredient.id)
                }
                for((i, id) in _ids.withIndex()){
                    Log.d("Recipe Items", _names[i] + " - " + _ids[i].toString())
                    if(fridgeIds.contains(id)){
                        _ingredients.add(Ingredient(id, "null", 0.0, "null", IngredientsData.map[id]!!, "", false, false))
                    }
                }
                _ingredientsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onClickStatus(position: Int) {
        _ingredients[position].obtained = !_ingredients[position].obtained
        _ingredientsRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnUpdateRecipeConfirmation ->{
                    GlobalScope.launch{
                        this?.let {
                            for(ingredient in _ingredients){
                                if(ingredient.obtained){
                                    _fridgeDao.deleteById(ingredient.id)
                                }
                            }
                        }
                        Handler(Looper.getMainLooper()).post{
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
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

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.recipe_confirmation_page_label)
                1 -> getString(R.string.page_cancel_label)
                2 -> getString(R.string.page_update_label)
                else -> getString(R.string.grocery_edit_input_warning_1)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.recipe_confirmation_page_label_sp)
                1 -> getString(R.string.page_cancel_label_sp)
                2 -> getString(R.string.page_update_label_sp)
                else -> getString(R.string.recipe_confirmation_page_label_sp)
            }
        }
        return "invalid"
    }
}