package com.recipeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.RawContacts.Data
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
import kotlinx.coroutines.processNextEventInCurrentThread
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

        var listOfNames = emptyList<String>().toMutableList()
        GlobalScope.launch {
            this?.let {
                listOfNames.addAll(_groceryListDao.getAllNames())
            }
        }

        cancelButton.setOnClickListener(){
            finish()
        }

        confirmButton.setOnClickListener(){
            if(name.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show()
            }else if(name.text.toString().length > 20) {
                Toast.makeText(this, "Please limit character length to 20 characters", Toast.LENGTH_SHORT).show()
            }else if(nameIsDuplicate(name.text.toString(
            ), listOfNames)){
                Toast.makeText(this, " A list of that name already exists!", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent()
                val currentDate = DateFormat.getDateInstance().format(Date())

                GlobalScope.launch{
                    this?.let{
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

}

