package com.recipeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.recipeme.R
import com.recipeme.adapters.GroceryListIngredientAdapter
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.interfaces.GroceryIngredientOnQuantityClick
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*


//need to map items
class GroceryListEditActivity : AppCompatActivity(), View.OnClickListener, GroceryIngredientOnQuantityClick {
    lateinit var listName: String
    //lateinit var ingredients: MutableList<Ingredient>
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var recyclerView: RecyclerView
    lateinit var listNameWarningString: TextView
    lateinit var ingredientWarningString: TextView
    lateinit var groceryListDao: GroceryListDao
    lateinit var listOfNames: MutableList<String>
    lateinit var oldList: GroceryList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list_edit)
        listName = intent.getStringExtra("ListName").toString()
        var listNameText = findViewById<TextView>(R.id.etGListNameGroceryListEdit)
        listNameText.setText(listName)
        listNameWarningString = findViewById<TextView>(R.id.tvListNameWarning)
        ingredientWarningString = findViewById<TextView>(R.id.tvIngredientWarning)
        listNameWarningString.text = ""
        ingredientWarningString.text = ""
        val cancelButton = findViewById<Button>(R.id.btnCancelGroceryListEdit).setOnClickListener(this)
        val saveButton = findViewById<Button>(R.id.btnSaveGroceryListEdit).setOnClickListener(this)
        // ingredients = mutableMapOf<Ingredient, Boolean>()
        ingredients = emptyList<Ingredient>().toMutableList()
        val addButton = findViewById<Button>(R.id.btnAddIngredientGroceryListEdit).setOnClickListener(this)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        groceryListDao = db.groceryListDao()

        val groceryListIngredientAdapter = GroceryListIngredientAdapter(ingredients, this)
        recyclerView= findViewById<RecyclerView>(R.id.rvGroceryListsList)
        recyclerView.adapter = groceryListIngredientAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        listOfNames = emptyList<String>().toMutableList()
        GlobalScope.launch {
            this?.let {
                listOfNames.addAll(groceryListDao.getAllNames())
                oldList = groceryListDao.getListFromName(listName)
                listOfNames.remove(listName)

                Handler(Looper.getMainLooper()).post{
                    ingredients.addAll(oldList.items)
                    Log.d(" checking", listName + " - " + oldList.items.toString())
                    recyclerView.adapter?.notifyDataSetChanged()
                }

            }
        }

    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnCancelGroceryListEdit->{
                    finish()
                }
                R.id.btnSaveGroceryListEdit->{
                    var newListName = findViewById<EditText>(R.id.etGListNameGroceryListEdit).text.toString()
                    var position = intent.getIntExtra("Position", 0)
                    if(newListName.length < 1) {
                        listNameWarningString.text = "Please enter a valid list name."
                    }else if(newListName.length > 20){
                        listNameWarningString.text = "Limit list name under 21 characters."
                    }else if(nameIsDuplicate(newListName, listOfNames)){
                        Toast.makeText(this, " A list of that name already exists!", Toast.LENGTH_SHORT).show()
                        Log.d("duplicate checking", listName + " - " + listOfNames)
                    }else{
                        val currentDate = DateFormat.getDateInstance().format(Date())
                        if(listName.compareTo(newListName) == 0){
                            GlobalScope.launch{
                                this?.let{
                                    oldList.timeCreated = currentDate
                                    oldList.items.clear()
                                    oldList.items.addAll(ingredients)
                                    groceryListDao.update(oldList)
                                }
                                Log.e("TAG", "List name not changed, ingredients changed: " + groceryListDao.getAll())
                            }
                        }else {
                            //edit for ingredients
                            GlobalScope.launch{
                                this?.let{
                                    groceryListDao.delete(oldList)
                                    //ToDo EDIT ID FIELD -----------------v
                                    groceryListDao.insertAll(GroceryList(newListName, currentDate, ingredients))
                                    Log.d("Testing updating new list", "Old: " + oldList.toString() + " \nNew: " + GroceryList(newListName, currentDate, ingredients).toString())
                                }
                                Log.e("TAG", "onViewCreated: " + groceryListDao.getAll())
                            }
                            intent.putExtra("Position", position)
                            intent.putExtra("ListName", newListName)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
                R.id.btnAddIngredientGroceryListEdit->{
                    var ingredientName = findViewById<EditText>(R.id.etIngredientGroceryListEdit).text.toString()
                    var ingredientQuantity = findViewById<EditText>(R.id.etIngredientUnitsGroceryListEdit).text.toString()
                    var ingredientUnitLabel = findViewById<TextView>(R.id.etIngredientUnitLabelGroceryListEdit).text.toString()
                    var duplicatePosition = isIngredientDuplicate(ingredientName)
                    Log.e("Adding", "Adding: " + ingredientName)
                    if(ingredientQuantity.length < 1){
                        ingredientWarningString.text = "Please enter valid quantity."
                    }else if(ingredientQuantity.toDouble() > 99){
                        ingredientWarningString.text = "Max quantity exceeded."
                    }else{
                        if(ingredientName.length < 1){
                            ingredientWarningString.text = "Please enter valid ingredient."
                        }else if(ingredientName.length > 20){
                            ingredientWarningString.text = "Limit name under 21 characters."
                        }else if(duplicatePosition !=-1){
                            if(isQuantityLabelSame(ingredientUnitLabel, duplicatePosition)){

                            }
                            ingredients[duplicatePosition].amount += ingredientQuantity.toDouble()
                            recyclerView.adapter?.notifyDataSetChanged()
                        }else{
                            ingredients.add(Ingredient("NULL", ingredientQuantity.toDouble(), "NULL", ingredientName, ingredientUnitLabel, false))
                            recyclerView.adapter?.notifyItemInserted(ingredients.size-1)
                        }
                    }
                    Log.e("Added", "Lis t: " + ingredients)

                }
            }
        }
    }

    fun isIngredientDuplicate(name: String): Int{
        var position = 0
        for(ingredient: Ingredient in ingredients) {
            if (name.compareTo(ingredient.name) == 0) {
                return position
            }
            position++
        }
        return -1
    }
    fun isQuantityLabelSame(label: String, position: Int):Boolean{
        //TODO <Must implement unit conversion>
        return true
    }

    override fun onClickDecrement(position: Int) {
        ingredients[position].amount--
        if(ingredients.get(position).amount < 1){
            ingredients.removeAt(position)
            recyclerView.adapter?.notifyItemRemoved(position)
        }else{
            recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    override fun onClickIncrement(position: Int) {
        ingredients[position].amount++
        recyclerView.adapter?.notifyItemChanged(position)
    }
    fun nameIsDuplicate(name: String, list: MutableList<String>):Boolean{
        return list.contains(name)
    }
}