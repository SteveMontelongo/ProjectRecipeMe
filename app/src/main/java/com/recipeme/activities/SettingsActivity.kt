package com.recipeme.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
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
    private lateinit var _pageLabel: TextView
    private lateinit var _buttonHoldLabelFridge: TextView
    private lateinit var _buttonHoldLabelGrocery: TextView
    private lateinit var _deleteFridge: Button
    private lateinit var _deleteGrocery: Button
    private lateinit var _themesBtn: Button
    private lateinit var _languagesBtn: Button
    private lateinit var _background: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "recipe-me-database").build()
        groceryListDao = db.groceryListDao()
        fridgeDao = db.fridgeDao()

        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val languageString = sharedPreferences.getString("language", "english")
        _background = findViewById<ImageView>(R.id.backgroundAppSettings)
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        setBackground(backgroundInt)

        _pageLabel = findViewById<TextView>(R.id.settingsLabel)
        _pageLabel.text = getMsg(0, languageString!!)
        _buttonHoldLabelFridge = findViewById<TextView>(R.id.fridgeDeleteLabel)
        _buttonHoldLabelFridge.text = getMsg(4, languageString!!)
        _buttonHoldLabelGrocery = findViewById<TextView>(R.id.groceryDeleteLabel)
        _buttonHoldLabelGrocery.text = getMsg(4, languageString!!)
        _deleteFridge = findViewById<Button>(R.id.btnDeleteFridgeSettings)
        _deleteFridge.text = getMsg(1, languageString!!)
        _deleteGrocery = findViewById<Button>(R.id.btnDeleteGrocerySettings)
        _deleteGrocery.text = getMsg(2, languageString!!)
        val cancelBtn = findViewById<ImageButton>(R.id.btnCancelSettings)
        _themesBtn = findViewById<Button>(R.id.btnThemesSettings)
        _themesBtn.text = getMsg(3, languageString!!)
        _languagesBtn = findViewById<Button>(R.id.btnLanguagesSettings)
        _languagesBtn.text = getMsg(5, languageString!!)

        cancelBtn.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
        _deleteFridge.setOnLongClickListener {
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
        _deleteGrocery.setOnLongClickListener {
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

        _themesBtn.setOnClickListener{
            var intent = Intent(this, ThemesActivity::class.java)
            resultSettingsLauncher.launch(intent)
        }

        _languagesBtn.setOnClickListener{
            var intent = Intent(this, LanguagesActivity::class.java)
            resultSettingsLauncher.launch(intent)
        }

    }

    private var resultSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        if(result.resultCode == Activity.RESULT_OK){
            val data:Intent? = result.data
            val languageString = sharedPreferences.getString("language", "english")
            _pageLabel.text = getMsg(0, languageString!!)
            _deleteFridge.text = getMsg(1, languageString!!)
            _deleteGrocery.text = getMsg(2, languageString!!)
            _buttonHoldLabelFridge.text = getMsg(4, languageString!!)
            _buttonHoldLabelGrocery.text = getMsg(4, languageString!!)
            _themesBtn.text = getMsg(3, languageString!!)
            _languagesBtn.text = getMsg(5, languageString!!)
        }
        val backgroundInt = sharedPreferences.getInt("background", R.drawable.recipe_me_plain)
        Log.d("Theme Test", backgroundInt.toString())
        setBackground(backgroundInt)
    }

    private fun getMsg(msgCode: Int, lang: String): String{
        if(lang == "english"){
            return when(msgCode){
                0 -> getString(R.string.settings_page_label)
                1 -> getString(R.string.settings_fridge_delete)
                2 -> getString(R.string.settings_grocery_delete)
                3 -> getString(R.string.settings_themes)
                4 -> getString(R.string.settings_hold_label)
                5 -> getString(R.string.settings_languages)
                else -> getString(R.string.settings_page_label)
            }
        }else if (lang == "spanish"){
            return when(msgCode){
                0 -> getString(R.string.settings_page_label_sp)
                1 -> getString(R.string.settings_fridge_delete_sp)
                2 -> getString(R.string.settings_grocery_delete_sp)
                3 -> getString(R.string.settings_themes_sp)
                4 -> getString(R.string.settings_hold_label_sp)
                5 -> getString(R.string.settings_languages_sp)
                else -> getString(R.string.settings_page_label_sp)
            }
        }
        return "invalid"
    }

    private fun setBackground(resId: Int){
        _background.setImageResource(resId)
    }
}