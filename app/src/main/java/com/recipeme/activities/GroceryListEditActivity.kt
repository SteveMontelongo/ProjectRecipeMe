package com.recipeme.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
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
import com.recipeme.utils.IngredientsData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class GroceryListEditActivity : AppCompatActivity(), View.OnClickListener, GroceryIngredientOnQuantityClick {
    private lateinit var _listName: String
    private lateinit var _ingredients: MutableList<Ingredient>
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _listNameWarningString: TextView
    private lateinit var _ingredientWarningString: TextView
    private lateinit var _groceryListDao: GroceryListDao
    private lateinit var _listOfNames: MutableList<String>
    private lateinit var _oldList: GroceryList
    private lateinit var _arrayIngredientNames: MutableList<String>
    private lateinit var _autoCompleteIngredientName: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list_edit)
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        _arrayIngredientNames = emptyList<String>().toMutableList()
        _ingredients = emptyList<Ingredient>().toMutableList()
        _listOfNames = emptyList<String>().toMutableList()
        for(ingredient in IngredientsData.map){
            _arrayIngredientNames.add(ingredient.value)
        }
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, _arrayIngredientNames)
        _autoCompleteIngredientName = findViewById<AutoCompleteTextView>(R.id.etIngredientGroceryListEdit)
        _autoCompleteIngredientName.hint = getMsg(6, languageString!!)
        _autoCompleteIngredientName.threshold = 1
        _autoCompleteIngredientName.setAdapter(arrayAdapter)
        _listName = intent.getStringExtra("ListName").toString()
        var listNameText = findViewById<TextView>(R.id.etGListNameGroceryListEdit)
        _listNameWarningString = findViewById<TextView>(R.id.tvListNameWarning)
        _ingredientWarningString = findViewById<TextView>(R.id.tvIngredientWarning)
        _listNameWarningString.text = ""
        _ingredientWarningString.text = ""
        listNameText.text = _listName
        listNameText.hint = getMsg(5, languageString!!)

        findViewById<ImageButton>(R.id.btnCancelGroceryListEdit).setOnClickListener(this)
        val saveBtn = findViewById<Button>(R.id.btnSaveGroceryListEdit)
        saveBtn.setOnClickListener(this)
        saveBtn.text = getMsg(7, languageString!!)
        findViewById<ImageButton>(R.id.btnAddIngredientGroceryListEdit).setOnClickListener(this)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        _groceryListDao = db.groceryListDao()

        val groceryListIngredientAdapter = GroceryListIngredientAdapter(_ingredients, this, this)
        _recyclerView= findViewById(R.id.rvGroceryListsList)
        _recyclerView.adapter = groceryListIngredientAdapter
        _recyclerView.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch {
            this?.let {
                _listOfNames.addAll(_groceryListDao.getAllNames())
                _oldList = _groceryListDao.getListFromName(_listName)
                _listOfNames.remove(_listName)

                Handler(Looper.getMainLooper()).post{
                    if(::_oldList.isInitialized){
                        _ingredients.addAll(_oldList.items)
                    }else{

                    }
                    _recyclerView.adapter?.notifyDataSetChanged()
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
                    val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                    val languageString = sharedPreferences.getString("language", "english")
                    var newListName = findViewById<EditText>(R.id.etGListNameGroceryListEdit).text.toString()
                    var position = intent.getIntExtra("Position", 0)
                    if(newListName.length < 1) {
                        _listNameWarningString.text = getMsg(0, languageString!!)
                    }else if(newListName.length > 20){
                        _listNameWarningString.text = getMsg(1, languageString!!)
                    }else if(nameIsDuplicate(newListName, _listOfNames)){
                        Toast.makeText(this, getMsg(2, languageString!!), Toast.LENGTH_SHORT).show()
                    }else{
                        _listNameWarningString.text = ""
                        val currentDate = DateFormat.getDateInstance().format(Date())
                        if(_listName.compareTo(newListName) == 0){
                            GlobalScope.launch{
                                this?.let{
                                    _oldList.timeCreated = currentDate
                                    _oldList.items.clear()
                                    _oldList.items.addAll(_ingredients)
                                    _groceryListDao.update(_oldList)
                                }
                            }
                        }else {
                            //edit for ingredients
                            GlobalScope.launch{
                                this?.let{
                                    if(::_oldList.isInitialized) {
                                        _groceryListDao.delete(_oldList)
                                    }
                                    _groceryListDao.insertAll(GroceryList(newListName, currentDate, _ingredients))
                                }
                            }
                            intent.putExtra("Position", position)
                            intent.putExtra("ListName", newListName)
                        }
                        Log.d("GroceryEdit", "Test " + position)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
                R.id.btnAddIngredientGroceryListEdit->{
                    val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                    val languageString = sharedPreferences.getString("language", "english")
                    hideSoftKeyboard()
                    var isCustom = false
                    val ingredientName = findViewById<EditText>(R.id.etIngredientGroceryListEdit).text.toString()
                    var ingredientQuantity = findViewById<EditText>(R.id.etIngredientUnitsGroceryListEdit)
                    val ingredientUnitLabel = findViewById<TextView>(R.id.etIngredientUnitLabelGroceryListEdit).text.toString()
                    val duplicatePosition = isIngredientDuplicate(ingredientName)
                    if(ingredientQuantity.text.toString() == "") ingredientQuantity.setText("1")
                    if(ingredientQuantity.text.isEmpty()){
                        _ingredientWarningString.text = getMsg(3, languageString!!)
                    }else if(ingredientQuantity.text.toString().toDouble() > 99){
                        _ingredientWarningString.text = getMsg(4, languageString!!)
                    }else{
//                        if(ingredientName.length < 1){
//                            _ingredientWarningString.text = "Please enter valid ingredient."
//                        }else if(ingredientName.length > 20){
//                            _ingredientWarningString.text = "Limit name under 21 characters."
//                        }else
                        if(!_arrayIngredientNames.contains(ingredientName)){
                            //_ingredientWarningString.text = "Please make a selection from the available ingredients."
                            isCustom = true
                        }
                        if(ingredientName.isEmpty()){
                            _ingredientWarningString.text = "Please enter a custom name or make a selection from the available ingredients."
                        }else if(duplicatePosition !=-1){
                            if(isQuantityLabelSame(ingredientUnitLabel, duplicatePosition)){

                            }
                            _ingredientWarningString.text = ""
                            _ingredients[duplicatePosition].amount += ingredientQuantity.text.toString().toDouble()
                            _recyclerView.adapter?.notifyDataSetChanged()
                        }else{
                            var key = 0
                            for(ingredient in IngredientsData.map){
                                if(ingredient.value.compareTo(ingredientName) != 0 ){
                                    continue;
                                }else{
                                    key = ingredient.key
                                }
                            }
                            _ingredientWarningString.text = ""
                            _ingredients.add(Ingredient(key,"NULL", ingredientQuantity.text.toString().toDouble(), "NULL", ingredientName, ingredientUnitLabel, false, isCustom))
                            _recyclerView.adapter?.notifyItemInserted(_ingredients.size-1)

                        }
                        _autoCompleteIngredientName.text.clear()
                        ingredientQuantity.setText("")
                    }
                }
            }
        }
    }

    private fun isIngredientDuplicate(name: String): Int{
        for((position, ingredient: Ingredient) in _ingredients.withIndex()) {
            if (name.compareTo(ingredient.name) == 0) {
                return position
            }
        }
        return -1
    }

    private fun isQuantityLabelSame(label: String, position: Int):Boolean {
        //quantity check function not used.
        return true
    }

    override fun onClickDecrement(position: Int) {
        _ingredients[position].amount--
        if(_ingredients[position].amount < 1){
            _ingredients.removeAt(position)
            _recyclerView.adapter?.notifyItemRemoved(position)
        }else{
            _recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    override fun onClickIncrement(position: Int) {
        _ingredients[position].amount++
        _recyclerView.adapter?.notifyItemChanged(position)
    }
    private fun nameIsDuplicate(name: String, list: MutableList<String>):Boolean{
        return list.contains(name)
    }

    fun hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.grocery_edit_input_warning_1)
                1 -> getString(R.string.grocery_edit_input_warning_2)
                2 -> getString(R.string.grocery_edit_input_warning_3)
                3 -> getString(R.string.grocery_edit_ingredient_warning_1)
                4 -> getString(R.string.grocery_edit_ingredient_warning_2)
                5 -> getString(R.string.grocery_edit_input_hint)
                6 -> getString(R.string.grocery_edit_input_ingredient_hint)
                7 -> getString(R.string.grocery_edit_save)
                else -> getString(R.string.grocery_edit_input_warning_1)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.grocery_edit_input_warning_1_sp)
                1 -> getString(R.string.grocery_edit_input_warning_2_sp)
                2 -> getString(R.string.grocery_edit_input_warning_3_sp)
                3 -> getString(R.string.grocery_edit_ingredient_warning_1_sp)
                4 -> getString(R.string.grocery_edit_ingredient_warning_2_sp)
                5 -> getString(R.string.grocery_edit_input_hint_sp)
                6 -> getString(R.string.grocery_edit_input_ingredient_hint_sp)
                7 -> getString(R.string.grocery_edit_save_sp)
                else -> getString(R.string.grocery_edit_input_warning_1_sp)
            }
        }
        return "invalid"
    }
}