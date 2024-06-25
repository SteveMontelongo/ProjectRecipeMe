package com.recipeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.recipeme.R
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.models.GroceryList
import com.recipeme.models.Ingredient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class PopupGroceryListsList : AppCompatActivity() {
    private lateinit var _groceryListDao: GroceryListDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_grocery_lists_list)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        _groceryListDao = db.groceryListDao()
        val name = findViewById<EditText>(R.id.etGListName)
        val cancelButton = findViewById<Button>(R.id.btnCancelPopUpGroceryListsList)
        val confirmButton = findViewById<Button>(R.id.btnConfirmPopUpGroceryListsList)
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        Log.d("Language test", languageString.toString())
        val listOfNames = emptyList<String>().toMutableList()
        GlobalScope.launch {
            this.let {
                listOfNames.addAll(_groceryListDao.getAllNames())
            }
        }

        cancelButton.setOnClickListener{
            finish()
        }

        confirmButton.setOnClickListener{
            if(name.text.toString().isEmpty()){
                Toast.makeText(this, getMsg(0, languageString!!), Toast.LENGTH_SHORT).show()
            }else if(name.text.toString().length > 20) {
                Toast.makeText(this, getMsg(1, languageString!!), Toast.LENGTH_SHORT).show()
            }else if(nameIsDuplicate(name.text.toString(), listOfNames)){
                Toast.makeText(this, getMsg(2, languageString!!), Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent()
                val currentDate = DateFormat.getDateInstance().format(Date())

                GlobalScope.launch{
                    this.let{
                        _groceryListDao.insertAll(GroceryList(name.text.toString(), currentDate, emptyList<Ingredient>().toMutableList()))
                    }
                }
                intent.putExtra("ListName", name.text.toString())
                intent.putExtra("ListDate", currentDate)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun nameIsDuplicate(name: String, list: MutableList<String>):Boolean{
        return list.contains(name)
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.grocery_edit_input_warning_1)
                1 -> getString(R.string.grocery_edit_input_warning_2)
                2 -> getString(R.string.grocery_edit_input_warning_3)
                else -> getString(R.string.grocery_edit_input_warning_1)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> "Por favor ingrese un nombre valido."
                1 -> "Limite la longitud de los caracteres a 20 caracteres."
                2 -> "Ya existe una lista con ese nombre!"
                else -> "Por favor ingrese un nombre valido."
            }
        }
        return "invalid"
    }

}

