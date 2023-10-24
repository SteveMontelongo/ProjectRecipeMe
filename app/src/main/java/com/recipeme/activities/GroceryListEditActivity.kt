package com.recipeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recipeme.R
import com.recipeme.adapters.GroceryListIngredientAdapter
import com.recipeme.interfaces.GroceryIngredientOnQuantityClick
import com.recipeme.models.Ingredient

class GroceryListEditActivity : AppCompatActivity(), View.OnClickListener, GroceryIngredientOnQuantityClick {
    lateinit var listName: String
    lateinit var ingredients: MutableList<Ingredient>
    lateinit var recyclerView: RecyclerView
    lateinit var listNameWarningString: TextView
    lateinit var ingredientWarningString: TextView
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
        ingredients = emptyList<Ingredient>().toMutableList()
        val addButton = findViewById<Button>(R.id.btnAddIngredientGroceryListEdit).setOnClickListener(this)

        val groceryListIngredientAdapter = GroceryListIngredientAdapter(ingredients, this)
        recyclerView= findViewById<RecyclerView>(R.id.rvGroceryListsList)
        recyclerView.adapter = groceryListIngredientAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                    if(listName.length < 1) {
                        listNameWarningString.text = "Please enter a valid list name."
                    }else if(listName.length > 20){
                        listNameWarningString.text = "Limit list name under 21 characters."
                    }else{
                        if(listName.compareTo(newListName) == 0){

                        }else {
                            //edit for ingredients
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
                            ingredients.add(Ingredient(1, "NULL", ingredientQuantity.toDouble(), "NULL", ingredientName, ingredientUnitLabel))
                            recyclerView.adapter?.notifyItemInserted(ingredients.size-1)
                        }
                    }

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
}