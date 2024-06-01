package com.recipeme.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import com.recipeme.R
import com.recipeme.daos.FridgeDao
import com.recipeme.daos.GroceryListDao
import com.recipeme.databases.AppDatabase
import com.recipeme.fragments.FridgeFragment
import com.recipeme.fragments.GroceryListFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity(){
    lateinit var groceryListDao: GroceryListDao
    lateinit var fridgeDao: FridgeDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        groceryListDao = db.groceryListDao()
        fridgeDao = db.fridgeDao()

        val deleteFridge = findViewById<Button>(R.id.btnDeleteFridgeSettings)
        val deleteGrocery = findViewById<Button>(R.id.btnDeleteGrocerySettings)
        val cancelBtn = findViewById<ImageButton>(R.id.btnCancelSettings)
        val themesBtn = findViewById<Button>(R.id.btnThemesSettings)
        cancelBtn.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
        deleteFridge.setOnLongClickListener {
            GlobalScope.launch {
                var ingredientIds = fridgeDao.getAllIds()
                for(id in ingredientIds){
                    fridgeDao.deleteById(id)
                }
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Fridge Deleted", Toast.LENGTH_LONG).show()
                }
            }
            true
        }
        deleteGrocery.setOnLongClickListener {
            GlobalScope.launch {
                var groceryLists = groceryListDao.getAll()
                for(list in groceryLists){
                    groceryListDao.delete(list)
                }
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "Grocery Lists Deleted", Toast.LENGTH_LONG).show()
                }
            }
            true
        }

        themesBtn.setOnClickListener{
            var intent = Intent(this, ThemesActivity::class.java)
            resultSettingsLauncher.launch(intent)
        }

    }

    private var resultSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data

        }
    }

}