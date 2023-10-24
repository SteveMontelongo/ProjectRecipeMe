package com.recipeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.adapters.GroceryListIngredientAdapter
import com.recipeme.adapters.GroceryListsListAdapter
import com.recipeme.interfaces.GroceryIngredientOnQuantityClick
import com.recipeme.models.Ingredient

class GroceryListEditActivity : AppCompatActivity(), View.OnClickListener, GroceryIngredientOnQuantityClick {
    lateinit var listName: String
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list_edit)
        listName = intent.getStringExtra("ListName").toString()
        val cancelButton = findViewById<Button>(R.id.btnCancelGroceryListEdit).setOnClickListener(this)
        val saveButton = findViewById<Button>(R.id.btnSaveGroceryListEdit).setOnClickListener(this)
        ingredients = emptyList<Ingredient>().toMutableList()
        val addButton = findViewById<Button>(R.id.btnAddIngredientGroceryListEdit).setOnClickListener(this)

        val groceryListIngredientAdapter = GroceryListIngredientAdapter(ingredients, this)
        recyclerView= findViewById<RecyclerView>(R.id.rvGroceryListsList)
        recyclerView.adapter = groceryListIngredientAdapter
        recyclerView.setLayoutManager(LinearLayoutManager(this))

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
                    if(listName.compareTo(newListName) == 0){

                    }else{
                        //edit for ingredients
                        intent.putExtra("Position", position)
                        intent.putExtra("ListName", newListName)
                    }
                }
                R.id.btnAddIngredientGroceryListEdit->{
                    var ingredientName = findViewById<EditText>(R.id.etIngredientGroceryListEdit).text.toString()
                    var ingredientQuantity = findViewById<EditText>(R.id.etIngredientUnitsGroceryListEdit).text.toString()
                    var ingredientUnitLabel = findViewById<TextView>(R.id.etIngredientUnitLabelGroceryListEdit).text.toString()

                    //edit HARDCODED
                    //TODO
                    ingredients.add(Ingredient(1, "NULL", ingredientQuantity.toDouble(), "NULL", ingredientName, ingredientUnitLabel))
                    recyclerView.adapter?.notifyItemInserted(ingredients.size-1)
                }
            }
        }
    }

    override fun onClickDecrement(position: Int) {
        ingredients.get(position).amount--
        if(ingredients.get(position).amount < 1){
            ingredients.removeAt(position)
            recyclerView.adapter?.notifyItemRemoved(position)
        }else{
            recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    override fun onClickIncrement(position: Int) {
        ingredients.get(position).amount++
        recyclerView.adapter?.notifyItemChanged(position)
    }
}